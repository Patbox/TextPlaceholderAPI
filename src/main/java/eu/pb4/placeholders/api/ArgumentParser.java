package eu.pb4.placeholders.api;

import eu.pb4.placeholders.api.arguments.StringArgs;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

public interface ArgumentParser<Result> {
    ArgumentParser<String> STRING = x -> Objects.requireNonNullElse(x, "");
    ArgumentParser<@Nullable String> NULLABLE_STRING = x -> x;
    ArgumentParser<StringArgs> STRING_ARGS_BASIC = x -> StringArgs.full(x, ' ', ':');

    Result parseArgument(@Nullable String string);
}
