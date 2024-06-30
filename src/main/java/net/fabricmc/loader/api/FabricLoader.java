package net.fabricmc.loader.api;

import eu.pb4.placeholders.api.arguments.StringArgs;

import java.util.List;
import java.util.Optional;

public class FabricLoader {
    private static final FabricLoader INSTANCE = new FabricLoader();
    public static FabricLoader getInstance() {
        return INSTANCE;
    }

    public boolean isDevelopmentEnvironment() {
        return false;
    }

    public Optional<ModContainer> getModContainer(String arg) {
        return Optional.empty();
    }

    public List<ModContainer> getAllMods() {
        return List.of();
    }
}
