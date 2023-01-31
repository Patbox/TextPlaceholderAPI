package eu.pb4.placeholders.impl.placeholder;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.util.Identifier;

public record ViewObjectImpl(Identifier identifier) implements PlaceholderContext.ViewObject {
}
