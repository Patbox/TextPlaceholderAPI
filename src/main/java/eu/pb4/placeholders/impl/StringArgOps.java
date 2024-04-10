package eu.pb4.placeholders.impl;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.arguments.StringArgs;

import java.util.stream.Stream;

public class StringArgOps implements DynamicOps<Either<String, StringArgs>> {
    public static final StringArgOps INSTANCE = new StringArgOps();
    @Override
    public Either<String, StringArgs> empty() {
        return Either.right(StringArgs.emptyNew());
    }

    @Override
    public <U> U convertTo(DynamicOps<U> outOps, Either<String, StringArgs> input) {
        return outOps.empty();
    }

    @Override
    public DataResult<Number> getNumberValue(Either<String, StringArgs> input) {
        try {
            if (input.left().isPresent()) {
                return DataResult.success(Double.valueOf(input.orThrow()));
            }
        } catch (Throwable ignored) {
            return DataResult.success(Boolean.parseBoolean(input.orThrow()) ? 1 : 0);
        }

        return DataResult.error(() -> input + " is not a number!");
    }

    @Override
    public Either<String, StringArgs> createNumeric(Number i) {
        return Either.left(i.toString());
    }

    @Override
    public DataResult<String> getStringValue(Either<String, StringArgs> input) {
        return input.left().isPresent() ? DataResult.success(input.left().get()) : DataResult.error(() -> input + " is not a number!");
    }

    @Override
    public Either<String, StringArgs> createString(String value) {
        return Either.left(value);
    }

    @Override
    public DataResult<Either<String, StringArgs>> mergeToList(Either<String, StringArgs> list, Either<String, StringArgs> value) {
        try {
            if (value.left().isPresent()) {
                list.right().get().unsafeOrdered().add(value.left().orElseThrow());
            } else {
                list.right().get().unsafeKeyedMap().put("" + list.right().get().unsafeKeyedMap().size(), value.right().orElseThrow());
            }

            return DataResult.success(list);
        } catch (Throwable e) {
            return DataResult.error(() -> list + " is not a list!");
        }
    }

    @Override
    public DataResult<Either<String, StringArgs>> mergeToMap(Either<String, StringArgs> map, Either<String, StringArgs> key, Either<String, StringArgs> value) {
        try {
            if (value.left().isPresent()) {
                map.right().get().unsafeKeyed().put(key.left().orElseThrow(), value.left().orElseThrow());
            } else {
                map.right().get().unsafeKeyedMap().put(key.left().orElseThrow(), value.right().orElseThrow());
            }

            return DataResult.success(map);
        } catch (Throwable e) {
            return DataResult.error(() -> key + " is not a correct key!");
        }
    }

    @Override
    public DataResult<Stream<Pair<Either<String, StringArgs>, Either<String, StringArgs>>>> getMapValues(Either<String, StringArgs> input) {
        try {
            return DataResult.success(
                    Stream.concat(
                            input.right().get().unsafeKeyed().entrySet().stream()
                                    .map((e) -> new Pair<>(Either.left(e.getKey()), Either.left(e.getValue()))),
                            input.right().get().unsafeKeyedMap().entrySet().stream()
                                    .map((e) -> new Pair<>(Either.left(e.getKey()), Either.right(e.getValue())))
                    )
            );
        } catch (Throwable e) {
            return DataResult.error(() -> input + " is not a map!");
        }
    }

    @Override
    public Either<String, StringArgs> createMap(Stream<Pair<Either<String, StringArgs>, Either<String, StringArgs>>> map) {
        var arg = StringArgs.emptyNew();
        map.forEach(x -> x.getSecond()
                .ifLeft(y -> arg.unsafeKeyed().put(x.getFirst().left().orElse(""), y))
                .ifRight(y -> arg.unsafeKeyedMap().put(x.getFirst().left().orElse(""), y)));
        return Either.right(arg);
    }

    @Override
    public DataResult<Stream<Either<String, StringArgs>>> getStream(Either<String, StringArgs> input) {
        return DataResult.success(input.left().isPresent() ? Stream.of(input) : Stream.concat(Stream.concat(
                input.right().get().unsafeKeyed().values().stream()
                        .map(Either::left),
                input.right().get().unsafeOrdered().stream()
                        .map(Either::left)),
                input.right().get().unsafeKeyedMap().values().stream()
                        .map(Either::right)
        ));
    }

    @Override
    public Either<String, StringArgs> createList(Stream<Either<String, StringArgs>> input) {
        var arg = StringArgs.emptyNew();
        input.forEach(x -> x.ifLeft(arg.unsafeOrdered()::add)
                .ifRight(y -> arg.unsafeKeyedMap().put("" + arg.unsafeKeyedMap().size(), y))
        );
        return Either.right(arg);
    }

    @Override
    public Either<String, StringArgs> remove(Either<String, StringArgs> input, String key) {
        input.ifRight(x -> {
            x.unsafeKeyed().remove(key);
            x.unsafeKeyedMap().remove(key);
        });

        return input;
    }
}
