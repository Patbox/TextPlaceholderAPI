package eu.pb4.placeholders.api.arguments;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SimpleArguments {
    public static boolean isWrapCharacter(char c) {
        return c == '"' || c == '\'' || c == '`';
    }

    public static String unwrap(String string) {
        if (string.length() < 2) {
            return string;
        }

        var c1 = string.charAt(0);
        var c2 = string.charAt(string.length() - 1);

        if (c1 == c2 && isWrapCharacter(c1)) {
            return string.substring(1, string.length() - 1);
        }
        return string;
    }

    public static List<String> split(String string, char separator) {
        return split(string, separator, true, true);
    }

    public static List<String> split(String string, char separator, boolean removeWrapping, boolean removeBackslash) {
        var list = new ArrayList<String>();
        var b = new StringBuilder();

        char wrap = 0;
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            if (character == '\\') {
                if (!removeBackslash) {
                    b.append(character);
                }

                if (i + 1 < string.length()) {
                    b.append(string.charAt(i + 1));
                    i += 1;
                }
                continue;
            }
            if (character == separator && wrap == 0) {
                list.add(b.toString());
                b = new StringBuilder();
                continue;
            }

            if (isWrapCharacter(character)) {
                if (wrap == 0) {
                    wrap = character;
                } else {
                    wrap = 0;
                }

                if (removeWrapping) {
                    continue;
                }
            }
            b.append(character);
        }

        if (!b.isEmpty()) {
            list.add(b.toString());
        }

        return list;
    }

    public static boolean bool(@Nullable String arg) {
        return bool(arg, false);
    }
    public static boolean bool(@Nullable String arg, boolean defaultBool) {
        if (arg == null || arg.isBlank()) {
            return defaultBool;
        }

        return switch (arg.toLowerCase(Locale.ROOT)) {
            case "true", "tru", "yes", "y", "1", "enabled", "enable", "on" -> true;
            default -> false;
        };
    }

    public static float floatNumber(@Nullable String arg) {
        return floatNumber(arg, 0);
    }
    public static float floatNumber(@Nullable String arg, float defaultFloat) {
        if (arg == null || arg.isBlank()) {
            return defaultFloat;
        }

        try {
            return Float.parseFloat(arg);
        } catch (Exception e) {
            return defaultFloat;
        }
    }

    public static int intNumber(@Nullable String arg) {
        return intNumber(arg, 0);
    }
    public static int intNumber(@Nullable String arg, int defaultFloat) {
        if (arg == null || arg.isBlank()) {
            return defaultFloat;
        }

        try {
            return Integer.parseInt(arg);
        } catch (Exception e) {
            return defaultFloat;
        }
    }
}
