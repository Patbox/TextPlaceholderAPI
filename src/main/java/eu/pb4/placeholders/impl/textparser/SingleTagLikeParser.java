package eu.pb4.placeholders.impl.textparser;

import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.parsers.TagLikeParser;

public class SingleTagLikeParser extends TagLikeParser {

    private final Format format;
    private final Provider provider;

    public SingleTagLikeParser(Format format, Provider provider) {
        this.format = format;
        this.provider = provider;
    }

    @Override
    protected void handleLiteral(String value, Context context) {
        int pos = 0;

        while (true) {
            var tag = this.format.findFirst(value, pos, provider, context);
            if (tag == null) {
                context.addNode(new LiteralNode(value.substring(pos)));
                break;
            } else if (tag.start() != 0 && tag.start() != pos) {
                context.addNode(new LiteralNode(value.substring(pos, tag.start())));
            }
            pos = tag.end();

            this.provider.handleTag(tag.id(), tag.argument(), context);
        }
    }

    public Format format() {
        return this.format;
    }

    public Provider provider() {
        return provider;
    }
}
