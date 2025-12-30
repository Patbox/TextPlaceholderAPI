package eu.pb4.placeholders.api;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public record Placeholder<Ctx, T>(Identifier identifier, ArgumentParser<T> argumentParser, Handler<Ctx, T> handler) {

    public PlaceholderResult onPlaceholderRequest(Ctx context, @Nullable String argument) {
        return handler.onPlaceholderRequest(context, argumentParser.parseArgument(argument));
    }

    public ArgumentedHandler<Ctx, T> withArgument(T argument) {
        return new ArgumentedHandler<>(this, argument);
    }

    public ArgumentedHandler<Ctx, T> withParsedArgument(@Nullable String argument) {
        return new ArgumentedHandler<>(this, this.argumentParser.parseArgument(argument));
    }

    public Placeholder<Ctx, T> withId(Identifier identifier) {
        return new Placeholder<>(identifier, argumentParser, handler);
    }

    @FunctionalInterface
    public interface Handler<Ctx, ArgType> {
        Handler<?, ?> EMPTY = (ctx, arg) -> PlaceholderResult.invalid();
        PlaceholderResult onPlaceholderRequest(Ctx context, ArgType argument);
    }

    public record ArgumentedHandler<Ctx, T>(Placeholder<Ctx, T> placeholder, T argument) {
        public PlaceholderResult onPlaceholderRequest(Ctx context) {
            return this.placeholder.handler.onPlaceholderRequest(context, argument);
        }
    }
}
