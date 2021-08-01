package eu.pb4.placeholders;

import eu.pb4.placeholders.builtin.PlayerPlaceholders;
import eu.pb4.placeholders.builtin.ServerPlaceholders;
import eu.pb4.placeholders.builtin.WorldPlaceholders;
import eu.pb4.placeholders.util.TextParserUtils;
import net.fabricmc.api.ModInitializer;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class PlaceholderAPIMod implements ModInitializer {
    @Override
    public void onInitialize() {
        TextParserUtils.register();
        ServerPlaceholders.register();
        PlayerPlaceholders.register();
        WorldPlaceholders.register();
    }
}
