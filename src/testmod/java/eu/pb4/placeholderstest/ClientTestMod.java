package eu.pb4.placeholderstest;

import eu.pb4.placeholders.api.client.ClientPlaceholderContext;
import eu.pb4.placeholders.api.node.parent.GradientNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;

import java.util.List;

public class ClientTestMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("test", "placeholders"), (graphics, deltaTracker) -> {
            var parsed = NodeParser.builder().clientPlaceholders().quickText().build().parseComponent(
                    """
                            <rb>Hello world!</>
                            You are %player:head% %player:name%
                            <gr yellow gold>Position: %player:pos_x% %player:pos_y% %player:pos_z% in %player:biome%</>
                            Game Time: %world:time%
                            Real Time: %server:time%
                            """, ClientPlaceholderContext.get().asParserContext());

            var t = MultiLineLabel.create(Minecraft.getInstance().font, parsed);
            t.visitLines(TextAlignment.LEFT, 8, 8, 10, graphics.textRenderer());

            var offset = System.currentTimeMillis() / 100;
            var colors = List.of(TextColor.fromRgb(0x6600cc), TextColor.fromRgb(0xcc99ff), TextColor.fromRgb(0x6600cc));
            var gr = GradientNode.GradientProvider.colors(colors);
            var g = GradientNode.apply(Component.literal("Gradient test for something"), (i, l) -> gr.getColorAt((int) ((i + offset) * 2 % l), l) );

            graphics.text(Minecraft.getInstance().font, g, 8, 8 + t.getLineCount() * 10 + 8, -1);
            graphics.text(Minecraft.getInstance().font, "" + offset, 8, 8 + t.getLineCount() * 10 + 18, -1);
        });
    }
}
