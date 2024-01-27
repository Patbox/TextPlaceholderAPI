package eu.pb4.placeholders.api.parsers.format;

public record SingleCharacterFormat(char start, char end, char argument, char[] argumentWrappers) implements BaseFormat {

    public SingleCharacterFormat(char start, char end) {
        this(start, end, (char) 0, DEFAULT_ARGUMENT_WRAPPER);
    }
    public SingleCharacterFormat(char start, char end, char argument) {
        this(start, end, argument, DEFAULT_ARGUMENT_WRAPPER);
    }

    @Override
    public boolean matchesStart(String string, int index) {
        return string.charAt(index) == this.start;
    }

    @Override
    public boolean matchesEnd(String string, int index) {
        return string.charAt(index) == this.end;

    }

    @Override
    public boolean matchesArgument(String string, int index) {
        return string.charAt(index) == this.argument;

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
        return this.argument != 0;
    }
}
