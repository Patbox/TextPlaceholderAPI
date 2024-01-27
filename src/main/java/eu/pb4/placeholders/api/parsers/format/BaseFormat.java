package eu.pb4.placeholders.api.parsers.format;

import eu.pb4.placeholders.api.parsers.TagLikeParser;
import org.jetbrains.annotations.Nullable;

public interface BaseFormat extends TagLikeParser.Format {
    char[] DEFAULT_ARGUMENT_WRAPPER = new char[]{'"', '\'', '`'};
    char[] LEGACY_ARGUMENT_WRAPPER = new char[]{'\''};

    boolean matchesStart(String string, int index);

    boolean matchesEnd(String string, int index);

    boolean matchesArgument(String string, int index);

    @Override
    @Nullable
    default TagLikeParser.Format.Tag findAt(String string, int start, TagLikeParser.Provider provider, TagLikeParser.Context context) {
        if (string.charAt(start) == '\\') {
            return null;
        }

        if (!this.matchesStart(string, start)) {
            return null;
        }

        String id = null;
        String argument = "";

        char wrapper = 0;
        var builder = new StringBuilder();
        int maxLengthEnd = string.length();
        validationLoop:
        for (int b = start + 1; b < maxLengthEnd; b++) {
            var curr = string.charAt(b);
            var matched = true;
            boolean isArgument = false;

            if (wrapper != 0) {
                if (curr == wrapper) {
                    wrapper = 0;
                }

                builder.append(curr);
                continue;
            }

            if (curr == '\\') {
                if (b + 1 < string.length()) {
                    b++;
                    builder.append(string.charAt(b));
                }

                continue;
            }

            if (id != null) {
                for (char argumentWrapper : this.argumentWrappers()) {
                    if (curr == argumentWrapper) {
                        builder.append(curr);
                        wrapper = curr;
                        continue validationLoop;
                    }
                }
            }

            if (id == null && this.hasArgument()) {
                isArgument = true;
                if (!this.matchesArgument(string, b)) {
                    matched = false;
                    isArgument = false;
                }
            }

            boolean isEnd = false;
            if (!isArgument) {
                matched = true;
                isEnd = true;
                if (!this.matchesEnd(string, b)) {
                    matched = false;
                    isEnd = false;
                }
            }

            if (matched) {
                var str = builder.toString();
                if (id == null) {
                    if (provider.isValidTag(str, context)) {
                        id = str;
                        builder = new StringBuilder();
                        if (!isEnd) {
                            continue;
                        }
                    } else {
                        return null;
                    }
                } else {
                    argument = str;
                }

                return new Tag(start, b + this.endLength(), id, argument);
            }

            builder.append(curr);
        }
        return null;
    }

    char[] argumentWrappers();

    int endLength();

    boolean hasArgument();
}
