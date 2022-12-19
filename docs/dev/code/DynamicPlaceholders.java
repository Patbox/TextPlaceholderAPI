import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.PlaceholderHandler;
import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.Random;


// --8<-- [start:dynamic]
public class DynamicPlaceholders {
    private static final Random random = new Random();

    /**
     * Example input:
     * <code>
     * Hello! ${player blue}. Random number between 0 and 20: ${random 20}
     * </code>
     *
     * Example output:
     * <code>
     * Hello! <blue>ThePlayerName</blue>. Random number: 13
     * </code>
     */
    public static Text parseInputText(ServerPlayerEntity player, Text inputText) {
        // parse the inputText message
        return Placeholders.parseText(inputText, PlaceholderContext.of(player),
                                      Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                                      DynamicPlaceholders::getPlaceholder);
    }

    private static PlaceholderHandler getPlaceholder(String id) {
        return switch (id) {
            case "player" -> DynamicPlaceholders::playerPlaceholder;
            case "random" -> DynamicPlaceholders::randomPlaceholder;
            default -> null;
        };
    }

    private static PlaceholderResult playerPlaceholder(PlaceholderContext ctx, String arg) {
        if (arg == null)
            return PlaceholderResult.invalid("No argument!");

        if (!ctx.hasPlayer())
            return PlaceholderResult.value(
                    Text.literal("You are not a player!")
                        .setStyle(Style.EMPTY.withColor(TextColor.parse(arg)))
                                          );

        return PlaceholderResult.value(
                ctx.player().getName().copy()
                   .setStyle(Style.EMPTY.withColor(TextColor.parse(arg)))
                                      );
    }

    private static PlaceholderResult randomPlaceholder(PlaceholderContext ctx, String arg) {
        if (arg == null) {
            int randomNumber = random.nextInt(10);
            return PlaceholderResult.value(String.valueOf(randomNumber));
        }

        try {
            int randomNumber = random.nextInt(Integer.parseInt(arg));
            return PlaceholderResult.value(String.valueOf(randomNumber));
        } catch (NumberFormatException e) {
            return PlaceholderResult.invalid("Invalid number!");
        }
    }
}
// --8<-- [end:dynamic]
