package eu.pb4.placeholders.impl.placeholder;

import eu.pb4.placeholders.api.PlaceholderContext;
import net.minecraft.resources.ResourceLocation;

public record ViewObjectImpl(ResourceLocation identifier) implements PlaceholderContext.ViewObject {
}
