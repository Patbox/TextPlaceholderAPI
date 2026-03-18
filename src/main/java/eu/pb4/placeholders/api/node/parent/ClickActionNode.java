package eu.pb4.placeholders.api.node.parent;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.impl.StringArgOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dialog.Dialog;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.Optional;

public final class ClickActionNode extends SimpleStylingNode {
    private static final HolderLookup.Provider DEFAULT_WRAPPER = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);
    private final ClickEvent.Action action;
    private final TextNode value;
    private final @Nullable Either<TextNode, StringArgs> data;

    public ClickActionNode(TextNode[] children, ClickEvent.Action action, TextNode value) {
        this(children, action, value, null);
    }

    public ClickActionNode(TextNode[] children, ClickEvent.Action action, TextNode value, @Nullable Either<TextNode, StringArgs> data) {
        super(children);
        this.action = action;
        this.value = value;
        this.data = data;
    }

    public ClickEvent.Action clickEventAction() {
        return action;
    }

    public TextNode value() {
        return value;
    }

    @Override
    protected Style style(ParserContext context) {
        return switch (this.action) {
            case OPEN_URL -> {
                try {
                    yield Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create(this.value.toComponent(context).getString())));
                } catch (Exception ignored) {
                    yield Style.EMPTY;
                }
            }
            case CHANGE_PAGE -> {
                try {
                    yield Style.EMPTY.withClickEvent(new ClickEvent.ChangePage(Integer.parseInt(this.value.toComponent(context).getString())));
                } catch (Exception ignored) {
                    yield Style.EMPTY;
                }
            }
            case OPEN_FILE ->
                    Style.EMPTY.withClickEvent(new ClickEvent.OpenFile(this.value.toComponent(context).getString()));
            case RUN_COMMAND ->
                    Style.EMPTY.withClickEvent(new ClickEvent.RunCommand(this.value.toComponent(context).getString()));
            case SUGGEST_COMMAND ->
                    Style.EMPTY.withClickEvent(new ClickEvent.SuggestCommand(this.value.toComponent(context).getString()));
            case COPY_TO_CLIPBOARD ->
                    Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard(this.value.toComponent(context).getString()));
            case CUSTOM -> {
                try {
                    HolderLookup.Provider wrapper;
                    if (context.contains(ParserContext.Key.HOLDER_LOOKUP)) {
                        wrapper = context.getOrThrow(ParserContext.Key.HOLDER_LOOKUP);
                    } else {
                        wrapper = DEFAULT_WRAPPER;
                    }

                    yield Style.EMPTY.withClickEvent(new ClickEvent.Custom(
                            Identifier.parse(this.value.toComponent(context).getString()),
                            this.data == null ? Optional.empty() : Optional.of(data.left().isPresent()
                                    ? TagParser.create(wrapper.createSerializationContext(NbtOps.INSTANCE)).parseFully(this.data.left().orElseThrow().toComponent(context).getString())
                                    : StringArgOps.INSTANCE.convertTo(NbtOps.INSTANCE, Either.right(this.data.right().orElseThrow()))
                            )
                    ));
                } catch (Throwable e) {
                    yield Style.EMPTY;
                }

            }
            case SHOW_DIALOG -> {
                HolderLookup.Provider wrapper;
                if (context.contains(ParserContext.Key.HOLDER_LOOKUP)) {
                    wrapper = context.getOrThrow(ParserContext.Key.HOLDER_LOOKUP);
                } else {
                    wrapper = DEFAULT_WRAPPER;
                }
                Holder<Dialog> dialogRegistryEntry = null;
                var data = this.value.toComponent(context).getString();

                var id = Identifier.tryParse(data);

                if (id != null) {
                    dialogRegistryEntry = wrapper.get(ResourceKey.create(Registries.DIALOG, id)).orElse(null);
                }

                if (dialogRegistryEntry == null) {
                    try {
                        dialogRegistryEntry = Dialog.CODEC.decode(
                                wrapper.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(data)).getOrThrow().getFirst();
                    } catch (Throwable e) {
                        // ignored
                    }

                }

                if (dialogRegistryEntry != null) {
                    yield Style.EMPTY.withClickEvent(new ClickEvent.ShowDialog(dialogRegistryEntry));
                } else {
                    yield Style.EMPTY;
                }
            }
        };
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new ClickActionNode(children, this.action, this.value, this.data);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        return new ClickActionNode(children, this.action, TextNode.asSingle(parser.parseNodes(this.value)),
                this.data != null && this.data.left().isPresent() ? Either.left(TextNode.asSingle(parser.parseNodes(this.data.left().orElseThrow()))) : this.data);
    }

    @Override
    public boolean isDynamicNoChildren() {
        return this.value.isDynamic() || (this.data != null && this.data.left().isEmpty() && this.data.left().orElseThrow().isDynamic());
    }

    @Override
    public String toString() {
        return "ClickActionNode{" +
                "action=" + action.getSerializedName() +
                ", value=" + value +
                ", data=" + data +
                '}';
    }
}
