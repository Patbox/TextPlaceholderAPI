package eu.pb4.placeholders;

import eu.pb4.placeholders.builtin.PlayerPlaceholders;
import eu.pb4.placeholders.builtin.ServerPlaceholders;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.regex.Pattern;


public class PlaceholderAPI implements ModInitializer {
	public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+:[^%]+)[%]");
	public static final Pattern ALT_PLACEHOLDER_PATTERN = Pattern.compile("[{]([^{}]+:[^{}]+)[}]");

	private static final HashMap<Identifier, PlaceholderHandler> PLACEHOLDERS = new HashMap<>();

	public static PlaceholderResult parsePlaceholder(PlaceholderContext context) {
		if (PLACEHOLDERS.containsKey(context.getIdentifier())) {
			return PLACEHOLDERS.get(context.getIdentifier()).PlaceholderHandler(context);
		} else {
			return PlaceholderResult.invalid("Placeholder doesn't exist!");
		}
	}

	public static String parseString(String text, ServerPlayerEntity player) {
		return Helpers.parseString(text, player, PLACEHOLDER_PATTERN);
	}

	public static String parseString(String text, MinecraftServer server) {
		return Helpers.parseString(text, server, PLACEHOLDER_PATTERN);
	}

	public static Text parseText(Text text, ServerPlayerEntity player) {
		return Helpers.recursivePlaceholderParsing(text, player, PLACEHOLDER_PATTERN);
	}

	public static Text parseText(Text text, MinecraftServer server) {
		return Helpers.recursivePlaceholderParsing(text, server, PLACEHOLDER_PATTERN);
	}

	public static String parseStringAlt(String text, ServerPlayerEntity player) {
		return Helpers.parseString(text, player, ALT_PLACEHOLDER_PATTERN);
	}

	public static String parseStringAlt(String text, MinecraftServer server) {
		return Helpers.parseString(text, server, ALT_PLACEHOLDER_PATTERN);
	}

	public static Text parseTextAlt(Text text, ServerPlayerEntity player) {
		return Helpers.recursivePlaceholderParsing(text, player, ALT_PLACEHOLDER_PATTERN);
	}

	public static Text parseTextAlt(Text text, MinecraftServer server) {
		return Helpers.recursivePlaceholderParsing(text, server, ALT_PLACEHOLDER_PATTERN);
	}

	public static void register(Identifier identifier, PlaceholderHandler handler) {
		PLACEHOLDERS.put(identifier, handler);
	}

	@Override
	public void onInitialize() {
		ServerPlaceholders.register();
		PlayerPlaceholders.register();
	}
}
