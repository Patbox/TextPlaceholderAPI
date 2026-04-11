package eu.pb4.placeholders.api.parsers;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.FinalizerNode;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.ResolutionContext;

import java.util.function.Function;


public record FinalWrappingParser(FinalizerNode.Finalizer finalizer, boolean isDynamic) implements NodeParser {
    public FinalWrappingParser(Function<Component, Component> function) {
        this((_, c) -> function.apply(c), false);
    }

    public FinalWrappingParser(FinalizerNode.Finalizer function) {
        this(function, true);
    }


    public static final FinalWrappingParser FLATTEN_COMPONENT = new FinalWrappingParser(x -> {
        var res = Component.empty();
        res.getSiblings().addAll(x.toFlatList());
        return res;
    });

    public static FinalWrappingParser resolving(ResolutionContext resolutionContext) {
        return new FinalWrappingParser(comp -> {
            try {
                return ComponentUtils.resolve(resolutionContext, comp);
            } catch (Throwable _) {
            }
            return comp;
        });
    }

    public static FinalWrappingParser resolving(ParserContext.Key<ResolutionContext> resolutionContextKey) {
        return new FinalWrappingParser((c, comp) -> {
            if (c.contains(resolutionContextKey)) {
                try {
                    return ComponentUtils.resolve(c.getOrThrow(resolutionContextKey), comp);
                } catch (Throwable _) {
                }
            }
            return comp;
        });
    }

    @Override
    public TextNode[] parseNodes(TextNode input) {
        return TextNode.array(new FinalizerNode(TextNode.array(input), this.finalizer, this.isDynamic));
    }
}
