package eu.pb4.placeholders.api;


import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PlaceholderHandler {
    PlaceholderResult onPlaceholderRequest(PlaceholderContext context, @Nullable String argument);
}
