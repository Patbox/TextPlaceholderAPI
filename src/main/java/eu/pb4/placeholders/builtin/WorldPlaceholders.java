package eu.pb4.placeholders.builtin;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderResult;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Identifier;
import net.minecraft.world.SpawnHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorldPlaceholders {
    static final int CHUNK_AREA = (int)Math.pow(17.0D, 2.0D);

    public static void register() {
        PlaceholderAPI.register(new Identifier("world", "time"), (ctx) -> {
            ServerWorld world;
            if (ctx.hasPlayer()) {
                world = ctx.getPlayer().getServerWorld();
            } else {
                world = ctx.getServer().getOverworld();
            }

            long dayTime = (long) (world.getTimeOfDay() * 3.6 / 60);

            return PlaceholderResult.value(String.format("%02d:%02d", (dayTime / 60 + 6) % 24, dayTime % 60));
        });

        PlaceholderAPI.register(new Identifier("world", "time_alt"), (ctx) -> {
            ServerWorld world;
            if (ctx.hasPlayer()) {
                world = ctx.getPlayer().getServerWorld();
            } else {
                world = ctx.getServer().getOverworld();
            }

            long dayTime = (long) (world.getTimeOfDay() * 3.6 / 60);
            long x = (dayTime / 60 + 6) % 24;
            long y = x % 12;
            if (y == 0) {
                y = 12;
            }
            return PlaceholderResult.value(String.format("%02d:%02d %s", y, dayTime % 60, x > 11 ? "PM" : "AM" ));
        });

        PlaceholderAPI.register(new Identifier("world", "day"), (ctx) -> {
            ServerWorld world;
            if (ctx.hasPlayer()) {
                world = ctx.getPlayer().getServerWorld();
            } else {
                world = ctx.getServer().getOverworld();
            }

            return PlaceholderResult.value("" + world.getTime() / 24000);
        });

        PlaceholderAPI.register(new Identifier("world", "id"), (ctx) -> {
            ServerWorld world;
            if (ctx.hasPlayer()) {
                world = ctx.getPlayer().getServerWorld();
            } else {
                world = ctx.getServer().getOverworld();
            }

            return PlaceholderResult.value(world.getRegistryKey().getValue().toString());
        });

        PlaceholderAPI.register(new Identifier("world", "name"), (ctx) -> {
            ServerWorld world;
            if (ctx.hasPlayer()) {
                world = ctx.getPlayer().getServerWorld();
            } else {
                world = ctx.getServer().getOverworld();
            }
            List<String> parts = new ArrayList<>();
            {
                String[] words = world.getRegistryKey().getValue().getPath().split("_");
                for (String word : words) {
                    String[] s = word.split("", 2);
                    s[0] = s[0].toUpperCase(Locale.ROOT);
                    parts.add(String.join("", s));
                }
            }
            return PlaceholderResult.value(String.join(" ", parts));
        });



        PlaceholderAPI.register(new Identifier("world", "player_count"), (ctx) -> {
            ServerWorld world;
            if (ctx.hasPlayer()) {
                world = ctx.getPlayer().getServerWorld();
            } else {
                world = ctx.getServer().getOverworld();
            }

            return PlaceholderResult.value("" + world.getPlayers().size());
        });

        PlaceholderAPI.register(new Identifier("world", "mob_count"), (ctx) -> {
            ServerWorld world;
            if (ctx.hasPlayer()) {
                world = ctx.getPlayer().getServerWorld();
            } else {
                world = ctx.getServer().getOverworld();
            }

            SpawnHelper.Info info = world.getChunkManager().getSpawnInfo();

            SpawnGroup spawnGroup = null;
            if (ctx.hasArgument()) {
                spawnGroup = SpawnGroup.byName(ctx.getArgument());
            }

            if (spawnGroup != null) {
                return PlaceholderResult.value("" + info.getGroupToCount().getInt(spawnGroup));
            } else {
                int x = 0;

                for (int value : info.getGroupToCount().values()) {
                    x += value;
                }
                return PlaceholderResult.value("" + x);
            }
        });

        PlaceholderAPI.register(new Identifier("world", "mob_cap"), (ctx) -> {
            ServerWorld world;
            if (ctx.hasPlayer()) {
                world = ctx.getPlayer().getServerWorld();
            } else {
                world = ctx.getServer().getOverworld();
            }

            SpawnHelper.Info info = world.getChunkManager().getSpawnInfo();

            SpawnGroup spawnGroup = null;
            if (ctx.hasArgument()) {
                spawnGroup = SpawnGroup.byName(ctx.getArgument());
            }

            if (spawnGroup != null) {
                return PlaceholderResult.value("" + spawnGroup.getCapacity() * info.getSpawningChunkCount() / CHUNK_AREA);
            } else {
                int x = 0;

                for (SpawnGroup group : SpawnGroup.values()) {
                    x += group.getCapacity();
                }
                return PlaceholderResult.value("" + x * info.getSpawningChunkCount() / CHUNK_AREA);
            }
        });
    }
}
