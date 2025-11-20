package eu.pb4.placeholders.impl.textparser.providers;

import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ColorNode;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import net.minecraft.network.chat.TextColor;

public record ModernProvider(TagRegistry registry) implements TagLikeParser.Provider {
    @Override
    public boolean isValidTag(String tag, TagLikeParser.Context context) {
        return tag.equals("/*")
                || tag.startsWith("#")
                || registry.getTag(tag) != null
                || tag.equals("/")
                || (tag.length() > 1 && tag.charAt(0) == '/' && context.contains(tag.substring(1)))
                || (tag.length() > 1 && tag.charAt(0) == ';' && context.contains(tag.substring(1)))
                ;
    }

    @Override
    public void handleTag(String id, String argument, TagLikeParser.Context context) {
        if (id.equals("/") || id.equals("/" + context.peekId()) || id.equals(";" + context.peekId())) {
            context.pop();
            return;
        } else if (id.equals("/*")) {
            context.pop(context.size());
            return;
        } else if (id.length() > 1 && id.charAt(0) == '/') {
            var s = id.substring(1);
            context.pop(s);
            return;
        } else if (id.length() > 1 && id.charAt(0) == ';') {
            var s = id.substring(1);
            context.popOnly(s);
            return;
        }

        if (id.startsWith("#")) {
            var text = TextColor.parseColor(id);
            if (text.result().isPresent()) {
                context.push(id, x -> new ColorNode(x, text.result().get()));
            }
            return;
        }


        var tag = registry.getTag(id);

        assert tag != null;

        var args = StringArgs.full(argument, ' ', ':');

        if (tag.selfContained()) {
            context.addNode(tag.nodeCreator().createTextNode(TextNode.array(), args, context.parser()));
        } else {
            context.push(id, (a) -> tag.nodeCreator().createTextNode(a, args, context.parser()));
        }
    }
}
