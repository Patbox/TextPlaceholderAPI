package com.mojang.authlib;

import java.util.UUID;

public record GameProfile(String name, UUID uuid) {
    public String getName() {
        return name;
    }

    public UUID getId() {
        return uuid;
    }
}
