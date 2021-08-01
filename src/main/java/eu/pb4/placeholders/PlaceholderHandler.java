package eu.pb4.placeholders;

@FunctionalInterface
public interface PlaceholderHandler {
    PlaceholderResult PlaceholderHandler(PlaceholderContext context);
}
