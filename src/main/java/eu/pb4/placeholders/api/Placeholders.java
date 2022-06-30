package eu.pb4.placeholders.api;

import com.google.common.collect.ImmutableMap;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.node.parent.ParentNode;
import eu.pb4.placeholders.impl.placeholder.builtin.PlayerPlaceholders;
import eu.pb4.placeholders.impl.placeholder.builtin.ServerPlaceholders;
import eu.pb4.placeholders.impl.placeholder.builtin.WorldPlaceholders;
import eu.pb4.placeholders.impl.placeholder.NodePlaceholderParserImpl;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public final class Placeholders {
	public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))[%](?<id>[^%]+:[^%]+)[%]");
	public static final Pattern ALT_PLACEHOLDER_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))[{](?<id>[^{}]+:[^{}]+)[}]");

	public static final Pattern PLACEHOLDER_PATTERN_CUSTOM = Pattern.compile("(?<!((?<!(\\\\))\\\\))[%](?<id>[^%]+)[%]");
	public static final Pattern ALT_PLACEHOLDER_PATTERN_CUSTOM = Pattern.compile("(?<!((?<!(\\\\))\\\\))[{](?<id>[^{}]+)[}]");

	public static final Pattern PREDEFINED_PLACEHOLDER_PATTERN = Pattern.compile("(?<!((?<!(\\\\))\\\\))\\$[{](?<id>[^}]+)}");

	private static final HashMap<Identifier, PlaceholderHandler> PLACEHOLDERS = new HashMap<>();

	private static final List<PlaceholderListChangedCallback> CHANGED_CALLBACKS = new ArrayList<>();

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

	public static ParentNode parseNodes(TextNode node, Pattern pattern, Set<String> placeholders, ParserContext.Key<PlaceholderGetter> key) {
		return new ParentNode(NodePlaceholderParserImpl.recursivePlaceholderParsing(node, pattern, new PlaceholderGetter() {
			@Override
			public PlaceholderHandler getPlaceholder(String placeholder, ParserContext context) {
				var get = context.get(key);
				return get != null ? get.getPlaceholder(placeholder, context) : null;
			}

			@Override
			public PlaceholderHandler getPlaceholder(String placeholder) {
				return placeholders.contains(placeholder) ? PlaceholderHandler.EMPTY : null;
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
		return parseNodes(TextNode.convert(text), pattern, placeholders).toText(ParserContext.of(), true);
	}

	public static Text parseText(Text text, Pattern pattern, Set<String> placeholders, ParserContext.Key<PlaceholderGetter> key) {
		return parseNodes(TextNode.convert(text), pattern, placeholders, key).toText(ParserContext.of(), true);
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
		return parseNodes(textNode, pattern, placeholders).toText(ParserContext.of(), true);
	}

	public static Text parseText(TextNode textNode, Pattern pattern, Set<String> placeholders, ParserContext.Key<PlaceholderGetter> key) {
		return parseNodes(textNode, pattern, placeholders, key).toText(ParserContext.of(), true);
	}

	/**
	 * Registers new placeholder for identifier
	 */
	public static void register(Identifier identifier, PlaceholderHandler handler) {
		PLACEHOLDERS.put(identifier, handler);
		for (var e : CHANGED_CALLBACKS) {
			e.onPlaceholderListChange(identifier, false);
		}
	}

	/**
	 * Removes placeholder
	 */
	public static void remove(Identifier identifier) {
		if (PLACEHOLDERS.remove(identifier) != null) {
			for (var e : CHANGED_CALLBACKS) {
				e.onPlaceholderListChange(identifier, true);
			}
		}
	}

	public static ImmutableMap<Identifier, PlaceholderHandler> getPlaceholders() {
		return ImmutableMap.copyOf(PLACEHOLDERS);
	}

	public static void registerChangeEvent(PlaceholderListChangedCallback callback) {
		CHANGED_CALLBACKS.add(callback);
	}

	public interface PlaceholderListChangedCallback {
		void onPlaceholderListChange(Identifier identifier, boolean removed);
	}

	public interface PlaceholderGetter {
		@Nullable
		PlaceholderHandler getPlaceholder(String placeholder);

		@Nullable
		default PlaceholderHandler getPlaceholder(String placeholder, ParserContext context) {
			return getPlaceholder(placeholder);
		}

		default boolean isContextOptional() {
			return false;
		}

		default boolean exists(String placeholder) {
			return this.getPlaceholder(placeholder) != null;
		}
	}

	static {
		PlayerPlaceholders.register();
		ServerPlaceholders.register();
		WorldPlaceholders.register();
	}
}
