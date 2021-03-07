package eu.pb4.placeholders;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PlaceholderAPI {
	public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%](?<id>[^%]+:[^%]+)[%]");
	public static final Pattern ALT_PLACEHOLDER_PATTERN = Pattern.compile("[{](?<id>[^{}]+:[^{}]+)[}]");

	public static final Pattern PLACEHOLDER_PATTERN_CUSTOM = Pattern.compile("[%](?<id>[^%]+)[%]");
	public static final Pattern ALT_PLACEHOLDER_PATTERN_CUSTOM = Pattern.compile("[{](?<id>[^{}]+)[}]");

	private static final HashMap<Identifier, PlaceholderHandler> PLACEHOLDERS = new HashMap<>();

	/**
	 * Parses PlaceholderContext, can be used for parsing by hand
	 *
	 * @return PlaceholderResult
	 */
	public static PlaceholderResult parsePlaceholder(PlaceholderContext context) {
		if (PLACEHOLDERS.containsKey(context.getIdentifier())) {
			return PLACEHOLDERS.get(context.getIdentifier()).PlaceholderHandler(context);
		} else {
			return PlaceholderResult.invalid("Placeholder doesn't exist!");
		}
	}

	/**
	 * Parses placeholders (without formatting) within String for player
	 * Placeholders have format of {@code %namespace:placeholder/argument%}
	 *
	 * @return String
	 */
	public static String parseString(String text, ServerPlayerEntity player) {
		return Helpers.parseString(text, player, PLACEHOLDER_PATTERN, PLACEHOLDERS);
	}

	/**
	 * Parses placeholders (without formatting) within String
	 * Placeholders have format of {@code %namespace:placeholder/argument%}
	 *
	 * @return String
	 */
	public static String parseString(String text, MinecraftServer server) {
		return Helpers.parseString(text, server, PLACEHOLDER_PATTERN, PLACEHOLDERS);
	}

	/**
	 * Parses placeholders for player
	 * Placeholders have format of {@code %namespace:placeholder/argument%}
	 *
	 * @return Text
	 */
	public static Text parseText(Text text, ServerPlayerEntity player) {
		return Helpers.recursivePlaceholderParsing(text, player, PLACEHOLDER_PATTERN, PLACEHOLDERS);
	}

	/**
	 * Parses placeholders
	 * Placeholders have format of {@code %namespace:placeholder/argument%}
	 *
	 * @return Text
	 */
	public static Text parseText(Text text, MinecraftServer server) {
		return Helpers.recursivePlaceholderParsing(text, server, PLACEHOLDER_PATTERN, PLACEHOLDERS);
	}

	/**
	 * Parses placeholders (without formatting) within String for player
	 * Placeholders have format of {@code \{namespace:placeholder/argument\}}
	 *
	 * @return String
	 */
	public static String parseStringAlt(String text, ServerPlayerEntity player) {
		return Helpers.parseString(text, player, ALT_PLACEHOLDER_PATTERN, PLACEHOLDERS);
	}

	/**
	 * Parses placeholders (without formatting) within String
	 * Placeholders have format of {@code \{namespace:placeholder/argument\}}
	 *
	 * @return String
	 */
	public static String parseStringAlt(String text, MinecraftServer server) {
		return Helpers.parseString(text, server, ALT_PLACEHOLDER_PATTERN, PLACEHOLDERS);
	}

	/**
	 * Parses placeholders for player
	 * Placeholders have format of {@code \{namespace:placeholder/argument\}}
	 *
	 * @return Text
	 */
	public static Text parseTextAlt(Text text, ServerPlayerEntity player) {
		return Helpers.recursivePlaceholderParsing(text, player, ALT_PLACEHOLDER_PATTERN, PLACEHOLDERS);
	}

	/**
	 * Parses placeholders
	 * Placeholders have format of {@code \{namespace:placeholder/argument\}}
	 *
	 * @return Text
	 */
	public static Text parseTextAlt(Text text, MinecraftServer server) {
		return Helpers.recursivePlaceholderParsing(text, server, ALT_PLACEHOLDER_PATTERN, PLACEHOLDERS);
	}

	/**
	 * Parses placeholders (without formatting) within String for player
	 * Placeholders have format of {@code %namespace:placeholder/argument%}
	 *
	 * @return String
	 */
	public static String parseStringCustom(String text, ServerPlayerEntity player, HashMap<Identifier, PlaceholderHandler> placeholders, Pattern pattern) {
		return Helpers.parseString(text, player, pattern, placeholders);
	}

	/**
	 * Parses custom placeholders (without formatting) within String
	 * Placeholders can have custom format
	 *
	 * @return String
	 */
	public static String parseStringCustom(String text, MinecraftServer server, HashMap<Identifier, PlaceholderHandler> placeholders, Pattern pattern) {
		return Helpers.parseString(text, server, pattern, placeholders);
	}

	/**
	 * Parses custom placeholders for player
	 * Placeholders can have custom format
	 *
	 * @return Text
	 */
	public static Text parseTextCustom(Text text, ServerPlayerEntity player, Map<Identifier, PlaceholderHandler> placeholders, Pattern pattern) {
		return Helpers.recursivePlaceholderParsing(text, player, pattern, placeholders);
	}

	/**
	 * Parses custom placeholders
	 * Placeholders can have custom format
	 *
	 * @return Text
	 */
	public static Text parseTextCustom(Text text, MinecraftServer server, Map<Identifier, PlaceholderHandler> placeholders, Pattern pattern) {
		return Helpers.recursivePlaceholderParsing(text, server, pattern, placeholders);
	}


	/**
	 * Registers new placeholder for identifier
	 */
	public static void register(Identifier identifier, PlaceholderHandler handler) {
		PLACEHOLDERS.put(identifier, handler);
	}
}
