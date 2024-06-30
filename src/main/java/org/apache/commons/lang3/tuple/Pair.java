package org.apache.commons.lang3.tuple;

import eu.pb4.placeholders.api.parsers.TagLikeParser;

import java.util.Map;

public class Pair<L, R> extends com.mojang.datafixers.util.Pair<L, R> implements Map.Entry<L, R> {
    public Pair(L first, R second) {
        super(first, second);
    }

    public static <L, R> Pair<L, R> of(Map.Entry<L, R> entry) {
        return new Pair<>(entry.getKey(), entry.getValue());
    }

    public L getLeft() {
        return this.getFirst();
    }

    public R getRight() {
        return this.getSecond();
    }

    @Override
    public L getKey() {
        return this.getLeft();
    }

    @Override
    public R getValue() {
        return this.getRight();
    }

    @Override
    public R setValue(R value) {
        return null;
    }


    public static <L, R> Pair<L, R> of(L l, R r) {
        return new Pair<>(l, r);
    }
}
