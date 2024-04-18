package eu.pb4.placeholders.api.parsers.tag;

import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.node.TextNode;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public record TextTag(String name, String[] aliases, String type, boolean userSafe, boolean selfContained,
                      NodeCreator nodeCreator) {
    public static TextTag self(String name, String type, Function<StringArgs, TextNode> creator) {
        return self(name, type, true, creator);
    }

    public static TextTag self(String name, String type, boolean userSafe, Function<StringArgs, TextNode> creator) {
        return self(name, List.of(), type, userSafe, creator);
    }

    public static TextTag self(String name, Collection<String> aliases, String type, boolean userSafe, Function<StringArgs, TextNode> creator) {
        return new TextTag(name, aliases.toArray(new String[0]), type, userSafe, true, NodeCreator.self(creator));
    }

    public static TextTag self(String name, String type, NodeCreator creator) {
        return self(name, type, true, creator);
    }

    public static TextTag self(String name, String type, boolean userSafe, NodeCreator creator) {
        return self(name, List.of(), type, userSafe, creator);
    }

    public static TextTag self(String name, Collection<String> aliases, String type, boolean userSafe, NodeCreator creator) {
        return new TextTag(name, aliases.toArray(new String[0]), type, userSafe, true, creator);
    }

    public static TextTag enclosing(String name, String type, NodeCreator creator) {
        return enclosing(name, type, true, creator);
    }

    public static TextTag enclosing(String name, String type, boolean userSafe, NodeCreator creator) {
        return enclosing(name, List.of(), type, userSafe, creator);
    }

    public static TextTag enclosing(String name, Collection<String> aliases, String type, boolean userSafe, NodeCreator creator) {
        return new TextTag(name, aliases.toArray(new String[0]), type, userSafe, false, creator);
    }
}
