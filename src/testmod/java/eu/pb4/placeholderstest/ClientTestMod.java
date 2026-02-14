package eu.pb4.placeholderstest;

import eu.pb4.placeholders.api.client.ClientPlaceholderContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.FormattedCharSink;

public class ClientTestMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("test", "placeholders"), (graphics, deltaTracker) -> {
            var parsed = NodeParser.builder().clientPlaceholders().quickText().build().parseComponent(
                    """
                            <rb>Hello world!</>
                            You are %player:head% %player:name%
                            <gr yellow gold>Position: %player:pos_x% %player:pos_y% %player:pos_z% in %player:biome%</>
                            Time: %world:time%
                            """, ClientPlaceholderContext.get().asParserContext());

            MultiLineLabel.create(Minecraft.getInstance().font, parsed).visitLines(TextAlignment.LEFT, 8, 8, 10, graphics.textRenderer());
        });
    }
}
