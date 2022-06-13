package eu.pb4.placeholders.api;


import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface PlaceholderHandler {
    PlaceholderHandler EMPTY = (ctx, arg) -> PlaceholderResult.invalid();
    PlaceholderResult onPlaceholderRequest(PlaceholderContext context, @Nullable String argument);
}
