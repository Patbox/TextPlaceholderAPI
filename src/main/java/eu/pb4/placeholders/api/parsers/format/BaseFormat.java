package eu.pb4.placeholders.api.parsers.format;

import eu.pb4.placeholders.api.parsers.TagLikeParser;
import org.jetbrains.annotations.Nullable;

public interface BaseFormat extends TagLikeParser.Format {
    char[] DEFAULT_ARGUMENT_WRAPPER = new char[]{'"', '\'', '`'};
    char[] LEGACY_ARGUMENT_WRAPPER = new char[]{'\''};

    int matchStart(String string, int index);

    int matchEnd(String string, int index);

    int matchArgument(String string, int index);

    @Override
    @Nullable
    default TagLikeParser.Format.Tag findAt(String string, int start, TagLikeParser.Provider provider, TagLikeParser.Context context) {
        if (string.charAt(start) == '\\') {
            return null;
        }

        var mStart = this.matchStart(string, start);

        if (mStart == 0) {
            return null;
        }

        String id = null;
        String argument = "";

        char wrapper = 0;
        var builder = new StringBuilder();
        int maxLengthEnd = string.length();
        validationLoop:
        for (int b = start + mStart; b < maxLengthEnd; b++) {
            var curr = string.charAt(b);
            var matched = true;
            int arg = 0;

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
                arg = this.matchArgument(string, b);
                if (arg <= 0) {
                    matched = false;
                    arg = 0;
                }
            }

            int end = 0;
            if (arg == 0) {
                matched = true;
                end = this.matchEnd(string, b);
                if (end <= 0) {
                    matched = false;
                    end = 0;
                }
            }

            if (matched) {
                var str = builder.toString();
                if (id == null) {
                    if (provider.isValidTag(str, context)) {
                        id = str;
                        builder = new StringBuilder();
                        if (end == 0) {
                            continue;
                        }
                    } else {
                        return null;
                    }
                } else {
                    argument = str;
                }

                return new Tag(start, b + end, id, argument);
            }

            builder.append(curr);
        }
        return null;
    }

    char[] argumentWrappers();

    int endLength();

    boolean hasArgument();
}
