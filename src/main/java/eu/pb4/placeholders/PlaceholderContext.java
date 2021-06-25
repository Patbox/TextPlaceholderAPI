package eu.pb4.placeholders;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public final class PlaceholderContext {
    private final Identifier identifier;
    private final String argument;
    private final boolean argumentExist;
    private final ServerPlayerEntity player;
    private final MinecraftServer server;
    private final boolean valid;

    private PlaceholderContext(Identifier identifier, String argument, ServerPlayerEntity player, MinecraftServer server, boolean valid) {
        this.identifier = identifier;
        if (argument == null) {
            this.argument = "";
            this.argumentExist = false;
        } else {
            this.argument = argument;
            this.argumentExist = true;
        }
        this.player = player;
        this.server = server;
        this.valid = valid;
    }

    /**
     * Allows to get identifier of placeholder
     *
     * @return Identifier
     */
    public Identifier getIdentifier() {
        return this.identifier;
    }

    /**
     * Allows to get used server
     *
     * @return MinecraftServer
     */
    public MinecraftServer getServer() {
        return this.server;
    }

    /**
     * Allows to get player from context
     *
     * @return ServerPlayerEntity or null
     */
    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    /**
     * Checks if player exist
     * @deprecated
     * <p> Use {@link PlaceholderContext#hasPlayer()} instead.
     *
     * @return boolean
     */
    @Deprecated
    public boolean playerExist() {
        return this.player != null;
    }

    /**
     * Returns argument used in placeholder
     *
     * @return String with argument or empty string
     */
    public String getArgument() {
        return this.argument;
    }

    /**
     * Allows to check, if argument was passed
     *
     * @return True if has argument, false if empty
     */
    public boolean hasArgument() { return this.argumentExist; }

    /**
     * Checks if placeholder is player specific
     *
     * @return boolean
     */
    public boolean hasPlayer() {
        return this.player != null;
    }

    /**
     * Checks if placeholder is valid
     *
     * @return boolean
     */
    public boolean isValid() {
        return this.valid;
    }

    /**
     * Creates context for placeholder (from string) and player
     *
     * @return PlaceholderContext
     */
    public static PlaceholderContext create(@NotNull String placeholder, @NotNull ServerPlayerEntity player) {
        String[] args = placeholder.split("/", 2);

        Identifier identifier = Identifier.tryParse(args[0]);

        if (identifier == null) {
            return new PlaceholderContext(null, null, player, player.server, false);
        } else {
            return new PlaceholderContext(identifier, args.length == 1 ? null : args[1], player, player.server, true);
        }
    }

    /**
     * Creates context for placeholder and player.
     * argument can be set to null
     *
     * @return PlaceholderContext
     */
    public static PlaceholderContext create(@NotNull Identifier identifier, String argument, @NotNull ServerPlayerEntity player) {
        return new PlaceholderContext(identifier, argument, player, player.server, true);
    }

    /**
     * Creates context for placeholder from string and server
     *
     * @return PlaceholderContext
     */
    public static PlaceholderContext create(@NotNull String placeholder, @NotNull MinecraftServer server) {
        String[] args = placeholder.split("/", 2);

        Identifier identifier = Identifier.tryParse(args[0]);

        if (identifier == null) {
            return new PlaceholderContext(null, null, null, server, false);
        } else {
            return new PlaceholderContext(identifier, args.length == 1 ? null : args[1], null, server, true);
        }
    }

    /**
     * Creates context for placeholder and server
     * argument can be set to null
     *
     * @return PlaceholderContext
     */
    public static PlaceholderContext create(@NotNull Identifier identifier, String argument, @NotNull MinecraftServer server) {
        return new PlaceholderContext(identifier, argument, null, server, true);
    }
}
