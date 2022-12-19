package kotlin

import eu.pb4.placeholders.api.PlaceholderContext
import eu.pb4.placeholders.api.PlaceholderHandler
import eu.pb4.placeholders.api.PlaceholderResult
import eu.pb4.placeholders.api.Placeholders
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import java.util.Random

// --8<-- [start:dynamic]
object DynamicPlaceholders {
    private val random = Random()
    
    /**
     * Example input:
     * ```
     * Hello! ${player blue}. Random number between 0 and 20: ${random 20}
     * ```
     *
     * Example output:
     * ```
     * Hello! <blue>ThePlayerName</blue>. Random number: 13
     * ```
     */
    fun parseInputText(player: ServerPlayerEntity, inputText: Text): Text {
        // parse the inputText message
        return Placeholders.parseText(inputText, PlaceholderContext.of(player),
                                      Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                                      DynamicPlaceholders::getPlaceholder)
    }
    
    private fun getPlaceholder(id: String): PlaceholderHandler? {
        return when (id) {
            "player" -> PlaceholderHandler(DynamicPlaceholders::playerPlaceholder)
            "random" -> PlaceholderHandler(DynamicPlaceholders::randomPlaceholder)
            else     -> null
        }
    }
    
    private fun playerPlaceholder(ctx: PlaceholderContext, arg: String?): PlaceholderResult {
        if (arg == null)
            return PlaceholderResult.invalid("No argument!")
        
        if (ctx.player == null)
            return PlaceholderResult.value(
                    Text.literal("You are not a player!")
                            .setStyle(Style.EMPTY.withColor(TextColor.parse(arg)))
                                          )
        
        return PlaceholderResult.value(
                ctx.player!!.name.copy()
                        .setStyle(Style.EMPTY.withColor(TextColor.parse(arg)))
                                      )
    }
    
    private fun randomPlaceholder(ctx: PlaceholderContext, arg: String?): PlaceholderResult {
        return try {
            val randomNumber = random.nextInt(arg?.toInt() ?: 10)
            PlaceholderResult.value(randomNumber.toString())
        } catch (e: NumberFormatException) {
            PlaceholderResult.invalid("Invalid number!")
        }
    }
}
// --8<-- [end:dynamic]
