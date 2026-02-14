package eu.pb4.placeholders.api.client;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.impl.client.ClientPlaceholderContextImpl;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.jspecify.annotations.Nullable;

@Environment(EnvType.CLIENT)
public interface ClientPlaceholderContext extends PlaceholderContext {
    ParserContext.Key<ClientPlaceholderContext> CLIENT_KEY = ParserContext.Key.of("client_placeholder_context", ClientPlaceholderContext.class);
    static ClientPlaceholderContext get() {
        return new ClientPlaceholderContextImpl(Minecraft.getInstance(), null);
    }

    @Nullable
    ClientLevel level();

    @Nullable
    LocalPlayer player();

    @Override
    ClientPlaceholderContext withView(ViewObject view);

    @Override
    default ParserContext asParserContext() {
        return ParserContext.of(CLIENT_KEY, this).with(COMMON_KEY, this).with(ParserContext.Key.HOLDER_LOOKUP, this.holderLookup());
    }

    @Override
    default void addToContext(ParserContext context) {
        context.with(CLIENT_KEY, this).with(COMMON_KEY, this);
        context.withIfNotSet(ParserContext.Key.HOLDER_LOOKUP, this.holderLookup());
    }
}
