package eu.pb4.placeholderstest;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import eu.pb4.placeholders.*;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;


public class TestMod implements ModInitializer {

    private static int test(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            player.sendMessage(PlaceholderAPI.parseText(context.getArgument("text", Text.class), player), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test2(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            Text text = TextParser.parse(context.getArgument("text", String.class));
            player.sendMessage(new LiteralText(Text.Serializer.toJson(text)), false);
            player.sendMessage(text, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test3(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            Text text = PlaceholderAPI.parseText(TextParser.parse(context.getArgument("text", String.class)), player);
            player.sendMessage(new LiteralText(Text.Serializer.toJson(text)), false);
            player.sendMessage(text, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test4(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            Text text = PlaceholderAPI.parsePredefinedText(
                    PlaceholderAPI.parseText(TextParser.parse(context.getArgument("text", String.class)), player),
                    PlaceholderAPI.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("player", player.getName())
                    );
            player.sendMessage(new LiteralText(Text.Serializer.toJson(text)), false);
            player.sendMessage(text, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                    literal("test").then(argument("text", TextArgumentType.text()).executes(TestMod::test))
            );

            dispatcher.register(
                    literal("test2").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test2))
            );

            dispatcher.register(
                    literal("test3").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test3))
            );
            dispatcher.register(
                    literal("test4").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test4))
            );
        });
    }

}
