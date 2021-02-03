package eu.pb4.placeholders;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderContext {
    private final Identifier identifier;
    private final String argument;
    private final ServerPlayerEntity player;
    private final MinecraftServer server;
    private final boolean valid;

    private PlaceholderContext(Identifier identifier, String argument, ServerPlayerEntity player, MinecraftServer server, boolean valid) {
        this.identifier = identifier;
        this.argument = argument;
        this.player = player;
        this.server = server;
        this.valid = valid;
    }

    public Identifier getIdentifier() {
        return this.identifier;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    public boolean playerExist() {
        return this.player != null;
    }

    public String getArgument() {
        return this.argument;
    }

    public boolean isValid() {
        return this.valid;
    }


    public static PlaceholderContext create(@NotNull String placeholder, @NotNull ServerPlayerEntity player) {
        String[] args = placeholder.split("/", 2);

        Identifier identifier = Identifier.tryParse(args[0]);

        if (identifier == null) {
            return new PlaceholderContext(null, "", player, player.server, false);
        } else {
            return new PlaceholderContext(identifier, args.length == 1 ? "" : args[1], player, player.server, true);
        }
    }

    public static PlaceholderContext create(@NotNull Identifier identifier, @NotNull String argument, @NotNull ServerPlayerEntity player) {
        return new PlaceholderContext(identifier, argument, player, player.server, true);
    }

    public static PlaceholderContext create(@NotNull String placeholder, @NotNull MinecraftServer server) {
        String[] args = placeholder.split("/", 1);

        Identifier identifier = Identifier.tryParse(args[0]);

        if (identifier == null) {
            return new PlaceholderContext(null, "", null, server, false);
        } else {
            return new PlaceholderContext(identifier, args.length == 1 ? "" : args[1], null, server, true);
        }
    }

    public static PlaceholderContext create(@NotNull Identifier identifier, String argument, @NotNull MinecraftServer server) {
        return new PlaceholderContext(identifier, argument == null ? "" : argument, null, server, true);
    }
}
