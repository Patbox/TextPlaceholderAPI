package eu.pb4.placeholders.api.node;

import com.mojang.datafixers.util.Either;
import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.class_10104;
import net.minecraft.text.Text;

public record ScoreNode(Either<class_10104, String> name, String objective) implements TextNode {

    public ScoreNode(String name, String objective) {
        this(class_10104.method_62667(name).result()
            .map(Either::<class_10104, String>left).orElse(Either.right(name)), objective);
    }

    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        return name.map(selector -> Text.method_62790(selector, objective), name -> Text.score(name, objective));
    }
}
