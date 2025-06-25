package eu.pb4.placeholders.api.node.parent;

import com.google.gson.JsonParser;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.impl.GeneralUtils;
import eu.pb4.placeholders.impl.StringArgOps;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtOperation;
import net.minecraft.nbt.SnbtParsing;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.util.Optional;

public final class ClickActionNode extends SimpleStylingNode {
    private final ClickEvent.Action action;
    private final TextNode value;
    private final @Nullable Either<TextNode, StringArgs> data;

    private static final RegistryWrapper.WrapperLookup DEFAULT_WRAPPER = DynamicRegistryManager.of(Registries.REGISTRIES);

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
                    yield  Style.EMPTY.withClickEvent(new ClickEvent.OpenUrl(URI.create(this.value.toText(context).getString())));
                } catch (Exception ignored) {
                    yield  Style.EMPTY;
                }
            }
            case CHANGE_PAGE -> {
                try {
                    yield Style.EMPTY.withClickEvent(new ClickEvent.ChangePage(Integer.parseInt(this.value.toText(context).getString())));
                } catch (Exception ignored) {
                    yield Style.EMPTY;
                }
            }
            case OPEN_FILE -> Style.EMPTY.withClickEvent(new ClickEvent.OpenFile(this.value.toText(context).getString()));
            case RUN_COMMAND -> Style.EMPTY.withClickEvent(new ClickEvent.RunCommand(this.value.toText(context).getString()));
            case SUGGEST_COMMAND -> Style.EMPTY.withClickEvent(new ClickEvent.SuggestCommand(this.value.toText(context).getString()));
            case COPY_TO_CLIPBOARD -> Style.EMPTY.withClickEvent(new ClickEvent.CopyToClipboard(this.value.toText(context).getString()));
            case CUSTOM -> {
                try {
                    RegistryWrapper.WrapperLookup wrapper;
                    if (context.contains(ParserContext.Key.WRAPPER_LOOKUP)) {
                        wrapper = context.getOrThrow(ParserContext.Key.WRAPPER_LOOKUP);
                    } else if (context.contains(PlaceholderContext.KEY)) {
                        wrapper = context.getOrThrow(PlaceholderContext.KEY).server().getRegistryManager();
                    } else {
                        wrapper = DEFAULT_WRAPPER;
                    }

                    yield Style.EMPTY.withClickEvent(new ClickEvent.Custom(
                            Identifier.of(this.value.toText(context).getString()),
                            this.data == null ? Optional.empty() : Optional.of(data.left().isPresent()
                                    ? StringNbtReader.fromOps(wrapper.getOps(NbtOps.INSTANCE)).read(this.data.left().orElseThrow().toText(context).getString())
                                    : StringArgOps.INSTANCE.convertTo(NbtOps.INSTANCE, Either.right(this.data.right().orElseThrow()))
                                    )
                    ));
                } catch (Throwable e) {
                    yield Style.EMPTY;
                }

            }
            case SHOW_DIALOG -> {
                RegistryWrapper.WrapperLookup wrapper;
                if (context.contains(ParserContext.Key.WRAPPER_LOOKUP)) {
                    wrapper = context.getOrThrow(ParserContext.Key.WRAPPER_LOOKUP);
                } else if (context.contains(PlaceholderContext.KEY)) {
                    wrapper = context.getOrThrow(PlaceholderContext.KEY).server().getRegistryManager();
                } else {
                    wrapper = DEFAULT_WRAPPER;
                }
                RegistryEntry<Dialog> dialogRegistryEntry = null;
                var data = this.value.toText(context).getString();

                var id = Identifier.tryParse(data);

                if (id != null) {
                    dialogRegistryEntry = wrapper.getOptionalEntry(RegistryKey.of(RegistryKeys.DIALOG, id)).orElse(null);
                }

                if (dialogRegistryEntry == null) {
                    try {
                        dialogRegistryEntry =  Dialog.ENTRY_CODEC.decode(
                                wrapper.getOps(JsonOps.INSTANCE), JsonParser.parseString(data)).getOrThrow().getFirst();
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
                "action=" + action.asString() +
                ", value=" + value +
                ", data=" + data +
                '}';
    }


    @Deprecated(forRemoval = true)
    public ClickActionNode(TextNode[] children, Action action, TextNode value) {
        super(children);
        this.action = action.vanillaType();
        this.value = value;
        this.data = null;
    }

    @Deprecated(forRemoval = true)
    public Action action() {
        return switch (this.action) {
            case OPEN_URL -> Action.OPEN_URL;
            case OPEN_FILE -> Action.OPEN_FILE;
            case CHANGE_PAGE -> Action.CHANGE_PAGE;
            case RUN_COMMAND -> Action.RUN_COMMAND;
            case SUGGEST_COMMAND -> Action.SUGGEST_COMMAND;
            case COPY_TO_CLIPBOARD -> Action.COPY_TO_CLIPBOARD;
            case SHOW_DIALOG -> Action.SHOW_DIALOG;
            case CUSTOM -> Action.CUSTOM;
        };
    }
    @Deprecated(forRemoval = true)
    public record Action(ClickEvent.Action vanillaType) {
        public static final Action OPEN_URL = new Action(ClickEvent.Action.OPEN_URL);
        public static final Action CHANGE_PAGE = new Action(ClickEvent.Action.CHANGE_PAGE);
        public static final Action OPEN_FILE = new Action(ClickEvent.Action.OPEN_FILE);
        public static final Action RUN_COMMAND = new Action(ClickEvent.Action.RUN_COMMAND);
        public static final Action SUGGEST_COMMAND = new Action(ClickEvent.Action.SUGGEST_COMMAND);
        public static final Action COPY_TO_CLIPBOARD = new Action(ClickEvent.Action.COPY_TO_CLIPBOARD);
        public static final Action SHOW_DIALOG = new Action(ClickEvent.Action.SHOW_DIALOG);
        public static final Action CUSTOM = new Action(ClickEvent.Action.CUSTOM);
    }
}
