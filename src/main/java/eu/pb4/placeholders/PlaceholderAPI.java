package eu.pb4.placeholders;

import eu.pb4.placeholders.buildin.PlayerPlaceholders;
import eu.pb4.placeholders.buildin.ServerPlaceholders;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.regex.Pattern;


public class PlaceholderAPI implements ModInitializer {
	public static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("[%]([^%]+:[^%]+)[%]");

	private static final HashMap<Identifier, PlaceholderHandler> PLACEHOLDERS = new HashMap<>();

	public static PlaceholderResult parsePlaceholder(PlaceholderContext context) {
		if (PLACEHOLDERS.containsKey(context.getIdentifier())) {
			return PLACEHOLDERS.get(context.getIdentifier()).PlaceholderHandler(context);
		} else {
			return PlaceholderResult.invalid("Placeholder doesn't exist!");
		}
	}

	public static String parseString(String text, ServerPlayerEntity player) {
		return Helpers.parseString(text, player);
	}

	public static String parseString(String text, MinecraftServer server) {
		return Helpers.parseString(text, server);

	}

	public static Text parseText(Text text, ServerPlayerEntity player) {
		return Helpers.recursivePlaceholderParsing(text, player);
	}

	public static Text parseText(Text text, MinecraftServer server) {
		return Helpers.recursivePlaceholderParsing(text, server);
	}

	public static void register(Identifier identifier, PlaceholderHandler handler) {
		PLACEHOLDERS.put(identifier, handler);
	}

	@Override
	public void onInitialize() {
		Commands.register();
		ServerPlaceholders.register();
		PlayerPlaceholders.register();
	}
}
