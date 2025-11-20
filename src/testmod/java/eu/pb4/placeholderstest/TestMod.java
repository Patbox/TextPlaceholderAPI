package eu.pb4.placeholderstest;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.TextParserUtils;
import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.api.node.LiteralNode;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.*;
import it.unimi.dsi.fastutil.Pair;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Map;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;


@SuppressWarnings("deprecation")
public class TestMod implements ModInitializer {
    private static int perf(CommandContext<CommandSourceStack> context) {
        var input = context.getArgument("text", String.class);
        ServerPlayer player = context.getSource().getPlayer();
        int iter = 1024 * 20;
        // old = NodeParser.merge(TextParserV1.DEFAULT, MarkdownLiteParserV1.ALL, LegacyFormattingParser.ALL)

        for (var pair : List.of(
                Pair.of(TextParserV1.DEFAULT, Placeholders.DEFAULT_PLACEHOLDER_PARSER),
                Pair.of(TagParser.SIMPLIFIED_TEXT_FORMAT, TagLikeParser.of(TagLikeParser.PLACEHOLDER,
                        TagLikeParser.Provider.placeholder(PlaceholderContext.KEY, Placeholders.DEFAULT_PLACEHOLDER_GETTER))),
                Pair.of(NodeParser.merge(TagParser.SIMPLIFIED_TEXT_FORMAT, TagLikeParser.of(TagLikeParser.PLACEHOLDER,
                        TagLikeParser.Provider.placeholder(PlaceholderContext.KEY, Placeholders.DEFAULT_PLACEHOLDER_GETTER))), NodeParser.NOOP)
        )) {
            player.sendSystemMessage(Component.literal("Parser: " + pair), false);
            long placeholderTimeTotal = 0;
            long contextTimeTotal = 0;
            long tagTimeTotal = 0;
            long textTimeTotal = 0;
            Component output = null;

            var parser = pair.left();
            var placeholder = pair.right();

            try {
                for (int i = 0; i < iter; i++) {
                    var time = System.nanoTime();
                    var tags = TextNode.asSingle(parser.parseNodes(new LiteralNode(input)));
                    tagTimeTotal += System.nanoTime() - time;
                    time = System.nanoTime();

                    var placeholders = TextNode.asSingle(placeholder.parseNodes(tags));
                    placeholderTimeTotal += System.nanoTime() - time;
                    time = System.nanoTime();

                    var ctx = ParserContext.of(PlaceholderContext.KEY, PlaceholderContext.of(player));
                    contextTimeTotal += System.nanoTime() - time;
                    time = System.nanoTime();

                    Component text = placeholders.toText(ctx, true);
                    textTimeTotal += System.nanoTime() - time;
                    output = text;
                }
                long total = tagTimeTotal + placeholderTimeTotal + textTimeTotal + contextTimeTotal;

                //player.sendSystemMessage(Text.literal(toJsonString(output)), false);
                player.sendSystemMessage(ComponentUtils.updateForEntity(context.getSource(), output, context.getSource().getEntity(), 0), false);
                player.sendSystemMessage(Component.literal(
                        "<FULL> Tag: " + ((tagTimeTotal / 1000) / 1000d) + " ms | " +
                                "Context: " + ((contextTimeTotal / 1000) / 1000d) + " ms | " +
                                "Placeholder: " + ((placeholderTimeTotal / 1000) / 1000d) + " ms | " +
                                "Text: " + ((textTimeTotal / 1000) / 1000d) + " ms | " +
                                "All: " + ((total / 1000) / 1000d) + " ms"
                ), false);

                player.sendSystemMessage(Component.literal(
                        "<SINGLE> Tag: " + ((tagTimeTotal / iter / 1000) / 1000d) + " ms | " +
                                "Context: " + ((contextTimeTotal / iter / 1000) / 1000d) + " ms | " +
                                "Placeholder: " + ((placeholderTimeTotal / iter / 1000) / 1000d) + " ms | " +
                                "Text: " + ((textTimeTotal / iter / 1000) / 1000d) + " ms | " +
                                "All: " + ((total / iter / 1000) / 1000d) + " ms"
                ), false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    private static int argTest(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal(
                StringArgs.full(context.getArgument("arg", String.class), ' ', ':').toString()));
        return 0;
    }

    private static int test(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            player.sendSystemMessage(Placeholders.parseText(context.getArgument("text", Component.class), PlaceholderContext.of(player)), false);
            player.sendSystemMessage(Component.literal(TextNode.convert(context.getArgument("text", Component.class)).toString()), false);
            player.sendSystemMessage(Component.literal(Placeholders.parseNodes(TextNode.convert(context.getArgument("text", Component.class))).toString()), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int markqt(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            player.sendSystemMessage(NodeParser.builder().markdown().quickText().build().parseText(context.getArgument("text", String.class), ParserContext.of()), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test2(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            Component text = TextParserUtils.formatText(context.getArgument("text", String.class));
            player.sendSystemMessage(Component.literal(toJsonString(text, context.getSource().registryAccess())), false);
            player.sendSystemMessage(text, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String toJsonString(Component text, RegistryAccess registryManager) {
        return ComponentSerialization.CODEC.encodeStart(registryManager.createSerializationContext(JsonOps.INSTANCE), text).getOrThrow().toString();
    }

    private static int test2oldnew(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            var form = context.getArgument("text", String.class);
            Component text = TextParserV1.DEFAULT.parseNode(form).toText();
            Component text2 = TagParser.SIMPLIFIED_TEXT_FORMAT.parseNode(form).toText();
            player.sendSystemMessage(Component.literal("v1"), false);
            player.sendSystemMessage(text, false);
            player.sendSystemMessage(Component.literal("v2"), false);
            player.sendSystemMessage(text2, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test2oldnewjson(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            var form = context.getArgument("text", String.class);
            Component text = TextParserV1.DEFAULT.parseNode(form).toText();
            Component text2 = TagParser.SIMPLIFIED_TEXT_FORMAT.parseNode(form).toText();
            player.sendSystemMessage(Component.literal("v1"), false);
            player.sendSystemMessage(Component.literal(toJsonString(text, context.getSource().registryAccess())), false);
            player.sendSystemMessage(text, false);
            player.sendSystemMessage(Component.literal("v2"), false);
            player.sendSystemMessage(Component.literal(toJsonString(text2, context.getSource().registryAccess())), false);
            player.sendSystemMessage(text2, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test3(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            var time = System.nanoTime();
            var tags = TextNode.asSingle(
                    LegacyFormattingParser.ALL.parseNodes(
                            TextNode.asSingle(
                                    MarkdownLiteParserV1.ALL.parseNodes(
                                            TextNode.asSingle(
                                                    TagParser.DEFAULT.parseNodes(new LiteralNode(context.getArgument("text", String.class)))
                                            )
                                    )
                            )
                    )
            );
            var tagTime = System.nanoTime() - time;
            time = System.nanoTime();

            var placeholders = Placeholders.parseNodes(tags);
            var placeholderTime = System.nanoTime() - time;
            time = System.nanoTime();

            Component text = placeholders.toText(ParserContext.of(PlaceholderContext.KEY, PlaceholderContext.of(player)), true);
            var textTime = System.nanoTime() - time;

            player.sendSystemMessage(Component.literal(toJsonString(text, context.getSource().registryAccess())), false);
            player.sendSystemMessage(ComponentUtils.updateForEntity(context.getSource(), text, context.getSource().getEntity(), 0), false);
            player.sendSystemMessage(Component.literal(
                      "Tag: " + ((tagTime / 1000) / 1000d) + " ms | " +
                            "Placeholder: " + ((placeholderTime / 1000) / 1000d) + " ms | " +
                            "Text: " + ((textTime / 1000) / 1000d) + " ms | " +
                            "All: " + (((tagTime + placeholderTime + textTime) / 1000) / 1000d) + " ms"
                    ), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test4Text(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            Component text = Placeholders.parseText(
                    Placeholders.parseText(TextParserUtils.formatText(context.getArgument("text", String.class)), PlaceholderContext.of(player)),
                    Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("player", player.getName())
            );
            player.sendSystemMessage(Component.literal(toJsonString(text, context.getSource().registryAccess())), false);
            player.sendSystemMessage(text, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test4nodes(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            Component text = Placeholders.parseNodes(
                    Placeholders.parseNodes(TextParserUtils.formatNodes(context.getArgument("text", String.class))),
                    Placeholders.PREDEFINED_PLACEHOLDER_PATTERN,
                    Map.of("player", player.getName())
            ).toText(ParserContext.of(PlaceholderContext.KEY, PlaceholderContext.of(player)), true);
            player.sendSystemMessage(Component.literal(toJsonString(text, context.getSource().registryAccess())), false);
            player.sendSystemMessage(text, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test5(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            var form = context.getArgument("text", String.class);

            Component text2 = NodeParser.builder()
                    .globalPlaceholders()
                    .simplifiedTextFormat()
                    .build()
                    .parseText(form, PlaceholderContext.of(player).asParserContext());
            player.sendSystemMessage(Component.literal(toJsonString(text2, context.getSource().registryAccess())), false);
            player.sendSystemMessage(text2, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test6x(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();
            ParserContext parsingContext = ParserContext.of();
            parsingContext.with(ParserContext.Key.WRAPPER_LOOKUP, player.registryAccess());     // You need to use this for Hover Item to work
            var form = context.getArgument("text", String.class);
            player.sendSystemMessage(Component.literal("------------------------------"), false);
            player.sendSystemMessage(Component.literal("Input.   | " + form), false);
            player.sendSystemMessage(Component.literal("STF-V1 | ").append(TextParserV1.DEFAULT.parseText(form, parsingContext)), false);
            player.sendSystemMessage(Component.literal("STF-V2 | ").append(TagParser.SIMPLIFIED_TEXT_FORMAT.parseText(form, parsingContext)), false);
            player.sendSystemMessage(Component.literal("STF+QT | ").append(TagParser.QUICK_TEXT_WITH_STF.parseText(form, parsingContext)), false);
            player.sendSystemMessage(Component.literal("QT       | ").append(TagParser.QUICK_TEXT.parseText(form, parsingContext)), false);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test7(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = context.getSource().getPlayer();

            var text = Placeholders.parseText(Component.translatable("death.attack.outOfWorld", player.getDisplayName()), PlaceholderContext.of(player));
            player.sendSystemMessage(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static int test8(CommandContext<CommandSourceStack> context) {
        try {
            var parser = NodeParser.builder().quickText().globalPlaceholders().build();
            context.getSource().sendSystemMessage(parser.parseText(StringArgumentType.getString(context, "text"), PlaceholderContext.of(context.getSource()).asParserContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void onInitialize() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, dedicated) -> {
            dispatcher.register(
                    literal("test1").then(argument("text", ComponentArgument.textComponent(registryAccess)).executes(TestMod::test))
            );
            dispatcher.register(
                    literal("argtest").then(argument("arg", StringArgumentType.greedyString()).executes(TestMod::argTest))
            );
            dispatcher.register(
                    literal("test2").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test2))
            );

            dispatcher.register(
                    literal("test2oldnew").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test2oldnew))
            );

            dispatcher.register(
                    literal("test2oldnewj").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test2oldnewjson))
            );

            dispatcher.register(
                    literal("test3").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test3))
            );

            dispatcher.register(
                    literal("perm").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::perf))
            );
            dispatcher.register(
                    literal("test4text").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test4Text))
            );
            dispatcher.register(
                    literal("test4nodes").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test4nodes))
            );
            dispatcher.register(
                    literal("test5").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test5))
            );

            dispatcher.register(
                    literal("test6ohno").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test6x))
            );

            dispatcher.register(
                    literal("test7").executes(TestMod::test7)
            );

            dispatcher.register(
                    literal("test8").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::test8))
            );

            dispatcher.register(
                    literal("markqt").then(argument("text", StringArgumentType.greedyString()).executes(TestMod::markqt))
            );

        });
    }

}
