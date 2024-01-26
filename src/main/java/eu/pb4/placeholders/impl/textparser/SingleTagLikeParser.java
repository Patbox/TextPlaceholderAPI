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

        while (pos != -1) {
            pos = this.handleTag(value, pos, this.format.findFirst(value, pos, provider, context), provider, context);
        }
    }

    public Format format() {
        return this.format;
    }

    public Provider provider() {
        return provider;
    }
}
