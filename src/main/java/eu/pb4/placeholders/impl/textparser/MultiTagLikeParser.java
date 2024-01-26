package eu.pb4.placeholders.impl.textparser;

import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import org.apache.commons.lang3.tuple.Pair;

public class MultiTagLikeParser extends TagLikeParser {

    private final Pair<Format, Provider>[] pairs;

    public MultiTagLikeParser(Pair<Format, Provider>[] formatsAndProviders) {
        this.pairs = formatsAndProviders;
    }

    @Override
    protected void handleLiteral(String value, Context context) {
        int pos = 0;

        while (pos != -1) {
            Provider provider = null;
            Format.Tag tag = null;

            while (pos < value.length()) {
                for (var p : pairs) {
                    var tag1 = p.getLeft().findAt(value, pos, p.getRight(), context);
                    if (tag1 != null && (tag == null || tag1.start() < tag.start())) {
                        provider = p.getRight();
                        tag = tag1;
                    }
                }

                if (tag == null) {
                    pos++;
                } else {
                    break;
                }
            }
            pos = this.handleTag(value, pos, tag, provider, context);
        }
    }

    public Pair<Format, Provider>[] pairs() {
        return pairs;
    }
}