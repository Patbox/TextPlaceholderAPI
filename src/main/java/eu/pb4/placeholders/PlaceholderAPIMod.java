package eu.pb4.placeholders;

import eu.pb4.placeholders.builtin.PlayerPlaceholders;
import eu.pb4.placeholders.builtin.ServerPlaceholders;
import net.fabricmc.api.ModInitializer;

public class PlaceholderAPIMod implements ModInitializer {
    @Override
    public void onInitialize() {
        ServerPlaceholders.register();
        PlayerPlaceholders.register();
    }
}
