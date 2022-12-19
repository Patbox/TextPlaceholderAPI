import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Map;


// --8<-- [start:static]
public class StaticPlaceholders {
    /**
     * Formats a player message according to <code>inputText</code>
     *
     * Example input:
     * <code>
     * ${playerName} says "${message}"
     * </code>
     *
     * @param inputText   The text that is parsed for placeholders.
     *                    Example input:
     *                    <code>${playerName} says "${message}"</code>
     * @param player      The player name used to replace the ${playerName}
     *                    variable in the input. Example input:
     *                    the player who's name is 'ThePlayerUsername'
     * @param messageText The message text used to replace the ${message}
     *                    variable in the input. Example input:
     *                    <code>this is the message</code>
     *
     * @return The formatted message. Example return:
     *         <code>ThePlayerUsername says "this is the message"</code>
     */
    public static Text formatPlayerMessage(Text inputText, ServerPlayerEntity player,
                                           Text messageText) {
        Map<String, Text> placeholders = Map.of(
                "message", messageText,        // replace ${message} with the messageText
                "playerName", player.getName() // replace ${playerName} with the player's name
                                               );

        return Placeholders.parseText(inputText,
                                      Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                                      placeholders); // parse the inputText
    }
}
// --8<-- [end:static]
