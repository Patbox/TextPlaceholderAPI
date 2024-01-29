package eu.pb4.placeholders.api.parsers.format;

public record MultiCharacterFormat(char[] start, char[] end, char[] argument, char[] argumentWrappers) implements BaseFormat {
    public MultiCharacterFormat(String start, String end, String argument) {
        this(start.toCharArray(), end.toCharArray(), argument.toCharArray(), DEFAULT_ARGUMENT_WRAPPER);
    }

    public MultiCharacterFormat(String start, String end, String argument, String argumentWrappers) {
        this(start.toCharArray(), end.toCharArray(), argument.toCharArray(), argumentWrappers.toCharArray());
    }

    @Override
    public int matchStart(String string, int index) {
        for (int a = 0; a < this.start.length; a++) {
            var charc = string.charAt(index + a);
            if (charc != this.start[a]) {
                return 0;
            }
        }
        return this.start.length;
    }

    @Override
    public int matchEnd(String string, int index) {
        for (int a = 0; a < this.end.length; a++) {
            var charc = string.charAt(index + a);
            if (charc != this.end[a]) {
                return 0;
            }
        }
        return this.end.length;
    }

    @Override
    public int matchArgument(String string, int index) {
        if (this.argument.length == 0) {
            return 0;
        }

        for (int a = 0; a < this.argument.length; a++) {
            var charc = string.charAt(index + a);
            if (charc != this.argument[a]) {
                return 0;
            }
        }
        return this.argument.length;
    }

    @Override
    public int endLength() {
        return this.end.length;
    }

    @Override
    public int index() {
        return -this.start.length;
    }

    @Override
    public boolean hasArgument() {
        return this.argument.length != 0;
    }
}
