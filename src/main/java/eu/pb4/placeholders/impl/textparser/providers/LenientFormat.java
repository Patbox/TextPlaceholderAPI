package eu.pb4.placeholders.impl.textparser.providers;

import eu.pb4.placeholders.api.parsers.format.BaseFormat;

public record LenientFormat() implements BaseFormat {
    public static final LenientFormat INSTANCE = new LenientFormat();

    @Override
    public int matchStart(String string, int index) {
        return string.charAt(index) == '<' ? 1 : 0;
    }

    @Override
    public int matchEnd(String string, int index) {
        return string.charAt(index) == '>' ? 1 : 0;

    }

    @Override
    public int matchArgument(String string, int index) {
        var c = string.charAt(index);
        return c == ':' || c == ' ' ? 1 : 0;
    }

    @Override
    public char[] argumentWrappers() {
        return BaseFormat.DEFAULT_ARGUMENT_WRAPPER;
    }

    @Override
    public int endLength() {
        return 1;
    }

    @Override
    public int index() {
        return -1;
    }

    @Override
    public boolean hasArgument() {
        return true;
    }
}
