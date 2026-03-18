package eu.pb4.placeholders.impl.placeholder;

import eu.pb4.placeholders.impl.ServerPlaceholderContextImpl;
import net.minecraft.resources.Identifier;

public record ViewObjectImpl(Identifier identifier) implements ServerPlaceholderContextImpl.ViewObject {
}
