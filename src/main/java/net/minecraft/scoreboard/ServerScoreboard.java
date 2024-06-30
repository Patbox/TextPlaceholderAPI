package net.minecraft.scoreboard;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.List;

public class ServerScoreboard {
    public static final ServerScoreboard INSTANCE = new ServerScoreboard();

    public ScoreboardObjective getNullableObjective(String arg) {
        return null;
    }

    public Collection<ScoreboardEntry> getScoreboardEntries(ScoreboardObjective scoreboardObjective) {
        return List.of();
    }

    public ReadableScoreboardScore getScore(ServerPlayerEntity player, ScoreboardObjective scoreboardObjective) {
        return ReadableScoreboardScore.INSTANCE;
    }
}
