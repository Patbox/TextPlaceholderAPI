package eu.pb4.placeholders.api.node;

import com.mojang.datafixers.util.Either;
import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CompilableString;

public record ScoreNode(Either<CompilableString<EntitySelector>, String> name, String objective) implements TextNode {

    public ScoreNode(String name, String objective) {
        this(Either.right(name), objective);
    }

    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        return name.map(selector -> Component.score(selector, objective), name -> Component.score(name, objective));
    }
}
