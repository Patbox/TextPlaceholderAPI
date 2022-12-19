package kotlin

import eu.pb4.placeholders.api.Placeholders
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text

// --8<-- [start:static]
object StaticPlaceholders {
    /**
     * Formats a player message according to [inputText]
     *
     * Example input:
     * ```
     * ${playerName} says "${message}"!
     * ```
     *
     * @param inputText The text that is parsed for placeholders.
     *                  Example input:
     *                  `${playerName} says "${message}"`
     *
     * @param player The player name used to replace the `${playerName}` variable in the input.
     *               Example input:
     *               the player who's name is `ThePlayerUsername`
     *
     * @param messageText The text used to replace the `${message}` variable in the input.
     *                    Example input:
     *                    `this is the message`
     *
     * @return The formatted message.
     *         Example return:
     *         ```
     *         ThePlayerUsername says "this is the message"!
     *         ```
     */
    fun formatPlayerMessage(inputText: Text?, player: ServerPlayerEntity, messageText: Text): Text {
        val placeholders = mapOf(
                "message" to messageText,    // replace ${message} with the messageText
                "playerName" to player.name, // replace ${playerName} with the player's name
                                )
        return Placeholders.parseText(inputText, Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                                      placeholders) // parse the inputText
    }
}
