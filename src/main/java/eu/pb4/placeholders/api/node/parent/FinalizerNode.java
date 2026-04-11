package eu.pb4.placeholders.api.node.parent;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import net.minecraft.network.chat.Component;

import java.util.function.Function;

public final class FinalizerNode extends ParentNode {
    private final Finalizer finalizer;
    private final boolean isDynamic;

    public FinalizerNode(TextNode[] children, Finalizer finalizer, boolean isDynamic) {
        super(children);
        this.finalizer = finalizer;
        this.isDynamic = true;
    }

    public FinalizerNode(TextNode[] children, Finalizer finalizer) {
        this(children, finalizer, true);
    }

    public FinalizerNode(TextNode[] children, Function<Component, Component> finalizer) {
        this(children, (_, c) -> finalizer.apply(c), false);
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new FinalizerNode(children, this.finalizer);
    }

    @Override
    public Component toComponent(ParserContext context, boolean removeBackslashes) {
        return this.finalizer.apply(context, super.toComponent(context, removeBackslashes));
    }

    @Override
    public boolean isDynamicNoChildren() {
        return this.isDynamic;
    }

    public interface Finalizer {
        Component apply(ParserContext context, Component component);
    }
}
