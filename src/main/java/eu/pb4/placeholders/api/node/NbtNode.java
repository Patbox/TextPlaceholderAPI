package eu.pb4.placeholders.api.node;

import com.mojang.datafixers.util.Either;
import eu.pb4.placeholders.api.ParserContext;
import java.util.Optional;

import eu.pb4.placeholders.impl.StringArgOps;
import net.minecraft.commands.arguments.NbtPathArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.NbtContents;
import net.minecraft.network.chat.contents.data.BlockDataSource;
import net.minecraft.network.chat.contents.data.EntityDataSource;
import net.minecraft.network.chat.contents.data.StorageDataSource;
import net.minecraft.resources.Identifier;

public record NbtNode(String dataSource, String path, String sourcePath, boolean interpret, boolean plain, Optional<TextNode> separator) implements TextNode {
    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        var lookup = context.get(ParserContext.Key.HOLDER_LOOKUP);
        var ops = lookup != null ? lookup.createSerializationContext(StringArgOps.INSTANCE) : StringArgOps.INSTANCE;

        var type = switch (this.dataSource) {
            case "block" -> new BlockDataSource(BlockDataSource.BLOCK_POS_CODEC.decode(ops, Either.left(sourcePath)).getOrThrow().getFirst());
            case "entity" -> new EntityDataSource(EntitySelector.COMPILABLE_CODEC.decode(ops, Either.left(sourcePath)).getOrThrow().getFirst());
            case "storage" -> new StorageDataSource(Identifier.tryParse(sourcePath));
            default -> null;
        };

        if (type == null) {
            return Component.empty();
        }


        return Component.nbt(NbtContents.NBT_PATH_CODEC.decode(ops, Either.left(path)).getOrThrow().getFirst(),
                interpret, plain, separator.map(x -> x.toComponent(context, removeBackslashes)), type);
    }

    @Override
    public boolean isDynamic() {
        return separator.isPresent() && separator.get().isDynamic();
    }
}
