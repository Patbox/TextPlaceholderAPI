package eu.pb4.placeholders.api.node;

import com.mojang.datafixers.util.Either;
import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.text.ParsedSelector;
import net.minecraft.text.Text;

public record ScoreNode(Either<ParsedSelector, String> name, String objective) implements TextNode {

    public ScoreNode(String name, String objective) {
        this(ParsedSelector.parse(name).result()
            .map(Either::<ParsedSelector, String>left).orElse(Either.right(name)), objective);
    }

    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        return name.map(selector -> Text.score(selector, objective), name -> Text.score(name, objective));
    }
}
