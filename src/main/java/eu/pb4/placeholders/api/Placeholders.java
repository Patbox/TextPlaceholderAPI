package eu.pb4.placeholders.api;

import com.google.common.collect.ImmutableMap;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.impl.placeholder.builtin.PlayerPlaceholders;
import eu.pb4.placeholders.impl.placeholder.builtin.ServerPlaceholders;
import eu.pb4.placeholders.impl.placeholder.builtin.WorldPlaceholders;
import eu.pb4.placeholders.impl.placeholder.NodePlaceholderParserImpl;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public final class Placeholders {
	public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(?<!\\\\)[%](?<id>[^%]+:[^%]+)[%]");
	public static final Pattern ALT_PLACEHOLDER_PATTERN = Pattern.compile("(?<!\\\\)[{](?<id>[^{}]+:[^{}]+)[}]");

	public static final Pattern PLACEHOLDER_PATTERN_CUSTOM = Pattern.compile("(?<!\\\\)[%](?<id>[^%]+)[%]");
	public static final Pattern ALT_PLACEHOLDER_PATTERN_CUSTOM = Pattern.compile("(?<!\\\\)[{](?<id>[^{}]+)[}]");

	public static final Pattern PREDEFINED_PLACEHOLDER_PATTERN = Pattern.compile("(?<!\\\\)\\$[{](?<id>[^}]+)}");

	private static final HashMap<Identifier, PlaceholderHandler> PLACEHOLDERS = new HashMap<>();

	private static final PlaceholderGetter PLACEHOLDER_GETTER = new PlaceholderGetter() {
		@Override
		public PlaceholderHandler getPlaceholder(String placeholder) {
			return PLACEHOLDERS.get(Identifier.tryParse(placeholder));
		}

		@Override
		public boolean isContextOptional() {
			return false;
		}
	};
	private static final PlaceholderContext EMPTY_CONTEXT = PlaceholderContext.of((MinecraftServer) null);

	/**
	 * Parses PlaceholderContext, can be used for parsing by hand
	 *
	 * @return PlaceholderResult
	 */
	public static PlaceholderResult parsePlaceholder(Identifier identifier, String argument, PlaceholderContext context) {
		if (PLACEHOLDERS.containsKey(identifier)) {
			return PLACEHOLDERS.get(identifier).onPlaceholderRequest(context, argument);
		} else {
			return PlaceholderResult.invalid("Placeholder doesn't exist!");
		}
	}

	/**
	 * Parses placeholders in nodes, without getting their final values
	 * Placeholders have format of {@code %namespace:placeholder argument%}
	 *
	 * @return Text
	 */
	public static ParentNode parseNodes(TextNode node) {
		return new ParentNode(NodePlaceholderParserImpl.recursivePlaceholderParsing(node, PLACEHOLDER_PATTERN, PLACEHOLDER_GETTER));
	}

	public static ParentNode parseNodes(TextNode node, Pattern pattern) {
		return new ParentNode(NodePlaceholderParserImpl.recursivePlaceholderParsing(node, pattern, PLACEHOLDER_GETTER));
	}

	public static ParentNode parseNodes(TextNode node, Pattern pattern, PlaceholderGetter placeholderGetter) {
		return new ParentNode(NodePlaceholderParserImpl.recursivePlaceholderParsing(node, pattern, placeholderGetter));
	}

	public static ParentNode parseNodes(TextNode node, Pattern pattern, Map<String, Text> placeholders) {
		return new ParentNode(NodePlaceholderParserImpl.recursivePlaceholderParsing(node, pattern, new PlaceholderGetter() {
			@Override
			public PlaceholderHandler getPlaceholder(String placeholder) {
				return (ctx, arg) -> PlaceholderResult.value(placeholders.get(placeholder));
			}

			@Override
			public boolean isContextOptional() {
				return true;
			}
		}));
	}


	/**
	 * Parses placeholders in text
	 * Placeholders have format of {@code %namespace:placeholder argument%}
	 *
	 * @return Text
	 */
	public static Text parseText(Text text, PlaceholderContext context) {
		return parseNodes(TextNode.convert(text)).toText(ParserContext.of(PlaceholderContext.KEY, context), true);
	}

	public static Text parseText(Text text, PlaceholderContext context, Pattern pattern) {
		return parseNodes(TextNode.convert(text), pattern).toText(ParserContext.of(PlaceholderContext.KEY, context), true);
	}

	public static Text parseText(Text text, PlaceholderContext context, Pattern pattern, PlaceholderGetter placeholderGetter) {
		return parseNodes(TextNode.convert(text), pattern, placeholderGetter).toText(ParserContext.of(PlaceholderContext.KEY, context), true);
	}

	public static Text parseText(Text text, Pattern pattern, Map<String, Text> placeholders) {
		return parseNodes(TextNode.convert(text), pattern, placeholders).toText(ParserContext.of(PlaceholderContext.KEY, Placeholders.EMPTY_CONTEXT), true);
	}

	public static Text parseText(TextNode textNode, PlaceholderContext context) {
		return parseNodes(textNode).toText(ParserContext.of(PlaceholderContext.KEY, context), true);
	}

	public static Text parseText(TextNode textNode, PlaceholderContext context, Pattern pattern) {
		return parseNodes(textNode, pattern).toText(ParserContext.of(PlaceholderContext.KEY, context), true);
	}

	public static Text parseText(TextNode textNode, PlaceholderContext context, Pattern pattern, PlaceholderGetter placeholderGetter) {
		return parseNodes(textNode, pattern, placeholderGetter).toText(ParserContext.of(PlaceholderContext.KEY, context), true);
	}

	public static Text parseText(TextNode textNode, PlaceholderContext context, Pattern pattern, Map<String, Text> placeholders) {
		return parseNodes(textNode, pattern, placeholders).toText(ParserContext.of(PlaceholderContext.KEY, context), true);
	}

	public static Text parseText(TextNode textNode, Pattern pattern, Map<String, Text> placeholders) {
		return parseNodes(textNode, pattern, placeholders).toText(ParserContext.of(PlaceholderContext.KEY, Placeholders.EMPTY_CONTEXT), true);
	}

	/**
	 * Registers new placeholder for identifier
	 */
	public static void register(Identifier identifier, PlaceholderHandler handler) {
		PLACEHOLDERS.put(identifier, handler);
	}

	/**
	 * Removes placeholder
	 */
	public static void remove(Identifier identifier) {
		PLACEHOLDERS.remove(identifier);
	}

	public static ImmutableMap<Identifier, PlaceholderHandler> getPlaceholders() {
		return ImmutableMap.copyOf(PLACEHOLDERS);
	}


	public interface PlaceholderGetter {
		PlaceholderHandler getPlaceholder(String placeholder);

		default boolean isContextOptional() {
			return false;
		}
	}

	static {
		PlayerPlaceholders.register();
		ServerPlaceholders.register();
		WorldPlaceholders.register();
	}
}
