package eu.pb4.placeholders.impl.textparser.providers;

import eu.pb4.placeholders.api.parsers.format.BaseFormat;

public record LenientFormat() implements BaseFormat {
    public static final LenientFormat INSTANCE = new LenientFormat();

    @Override
    public boolean matchesStart(String string, int index) {
        return string.charAt(index) == '<';
    }

    @Override
    public boolean matchesEnd(String string, int index) {
        return string.charAt(index) == '>';

    }

    @Override
    public boolean matchesArgument(String string, int index) {
        var c = string.charAt(index);
        return c == ':' || c == ' ';

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
    public int minLength() {
        return 2;
    }

    @Override
    public boolean hasArgument() {
        return true;
    }
}
