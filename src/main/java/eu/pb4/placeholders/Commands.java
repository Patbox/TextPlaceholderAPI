package eu.pb4.placeholders;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

/*
 * Just for testing, I will remove it later
 */

public class Commands {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("placeholder")
                            .executes(Commands::test)
                                    .then(argument("test", StringArgumentType.greedyString())
                                            .executes(Commands::test)
                                    )
            );

            dispatcher.register(
                    literal("placeholder2")
                            .executes(Commands::test)
                            .then(argument("test", TextArgumentType.text())
                                    .executes(Commands::test2)
                            )
            );


        });
    }

    public static int test(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        String test = context.getArgument("test", String.class);

        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            source.sendFeedback(new LiteralText(PlaceholderAPI.parseString(test, player)), false);
        } else {
            source.sendFeedback(new LiteralText("Only players can use this command!"), false);
        }

        return 0;
    }

    public static int test2(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        Text test = context.getArgument("test", LiteralText.class);

        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            player.sendMessage(PlaceholderAPI.parseText(test, player), false);
        } else {
            source.sendFeedback(new LiteralText("Only players can use this command!"), false);
        }

        return 0;
    }
}
