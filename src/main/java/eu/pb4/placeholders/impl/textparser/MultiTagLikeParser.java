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

        while (true) {
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

            if (tag == null) {
                context.addNode(new LiteralNode(value.substring(pos)));
                break;
            } else if (tag.start() != 0 && tag.start() != pos) {
                context.addNode(new LiteralNode(value.substring(pos, tag.start())));
            }
            pos = tag.end();

            provider.handleTag(tag.id(), tag.argument(), context);
        }
    }

    public Pair<Format, Provider>[] pairs() {
        return pairs;
    }
}
