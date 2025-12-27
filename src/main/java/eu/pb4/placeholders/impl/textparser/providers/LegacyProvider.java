package eu.pb4.placeholders.impl.textparser.providers;

import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ColorNode;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import eu.pb4.placeholders.api.parsers.tag.TagRegistry;
import net.minecraft.network.chat.TextColor;

public record LegacyProvider(TagRegistry registry) implements TagLikeParser.Provider {
    @Override
    public boolean isValidTag(String tag, TagLikeParser.Context context) {
        var peek = context.peekId();
        return tag.equals("r") || tag.equals("reset")
                || tag.startsWith("#")
                || registry.getTag(tag) != null
                || tag.equals("/") || (peek != null && tag.equals("/" + peek));
    }

    @Override
    public void handleTag(String id, String argument, TagLikeParser.Context context) {
        if (id.equals("/") || id.equals("/" + context.peekId())) {
            context.pop();
            return;
        }

        if (id.equals("r") || id.equals("reset")) {
            context.pop(context.size());
            return;
        }

        if (id.startsWith("#")) {
            var text = TextColor.parseColor(id);
            if (text.result().isPresent()) {
                context.push("c", x -> new ColorNode(x, text.result().get()));
            }
            return;
        }


        var tag = registry.getTag(id);

        assert tag != null;
        if (tag.selfContained()) {
            context.addNode(tag.nodeCreator().createTextNode(TextNode.array(), StringArgs.ordered(argument, ':'), context.parser()));
        } else {
            context.push(id, (a) -> tag.nodeCreator().createTextNode(a, StringArgs.ordered(argument, ':'), context.parser()));
        }
    }
}
