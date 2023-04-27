package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.SpawnHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorldPlaceholders {
    static final int CHUNK_AREA = (int)Math.pow(17.0D, 2.0D);

    public static void register() {
        Placeholders.register(new Identifier("world", "time"), (ctx, arg) -> {
            ServerWorld world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = ctx.server().getOverworld();
            }

            long dayTime = (long) (world.getTimeOfDay() * 3.6 / 60);

            return PlaceholderResult.value(String.format("%02d:%02d", (dayTime / 60 + 6) % 24, dayTime % 60));
        });

        Placeholders.register(new Identifier("world", "time_alt"), (ctx, arg) -> {
            ServerWorld world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = ctx.server().getOverworld();
            }

            long dayTime = (long) (world.getTimeOfDay() * 3.6 / 60);
            long x = (dayTime / 60 + 6) % 24;
            long y = x % 12;
            if (y == 0) {
                y = 12;
            }
            return PlaceholderResult.value(String.format("%02d:%02d %s", y, dayTime % 60, x > 11 ? "PM" : "AM" ));
        });

        Placeholders.register(new Identifier("world", "day"), (ctx, arg) -> {
            ServerWorld world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = ctx.server().getOverworld();
            }

            return PlaceholderResult.value("" + world.getTimeOfDay() / 24000);
        });

        Placeholders.register(new Identifier("world", "id"), (ctx, arg) -> {
            ServerWorld world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = ctx.server().getOverworld();
            }

            return PlaceholderResult.value(world.getRegistryKey().getValue().toString());
        });

        Placeholders.register(new Identifier("world", "name"), (ctx, arg) -> {
            ServerWorld world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = ctx.server().getOverworld();
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



        Placeholders.register(new Identifier("world", "player_count"), (ctx, arg) -> {
            ServerWorld world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = ctx.server().getOverworld();
            }

            return PlaceholderResult.value("" + world.getPlayers().size());
        });

        Placeholders.register(new Identifier("world", "mob_count_colored"), (ctx, arg) -> {
            ServerWorld world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = ctx.server().getOverworld();
            }

            SpawnHelper.Info info = world.getChunkManager().getSpawnInfo();

            SpawnGroup spawnGroup = null;
            if (arg != null) {
                spawnGroup = SpawnGroup.valueOf(arg.toUpperCase(Locale.ROOT));
            }

            if (spawnGroup != null) {
                int count = info.getGroupToCount().getInt(spawnGroup);
                int cap = spawnGroup.getCapacity() * info.getSpawningChunkCount() / CHUNK_AREA;

                return PlaceholderResult.value(count > 0 ? Text.literal("" + count).formatted(count > cap ? Formatting.LIGHT_PURPLE : count > 0.8 * cap ? Formatting.RED : count > 0.5 * cap ? Formatting.GOLD : Formatting.GREEN) : Text.literal("-").formatted(Formatting.GRAY));
            } else {
                int cap = 0;

                for (SpawnGroup group : SpawnGroup.values()) {
                    cap += group.getCapacity();
                }
                cap = cap * info.getSpawningChunkCount() / CHUNK_AREA;

                int count = 0;

                for (int value : info.getGroupToCount().values()) {
                    count += value;
                }
                return PlaceholderResult.value(count > 0 ? Text.literal("" + count).formatted(count > cap ? Formatting.LIGHT_PURPLE : count > 0.8 * cap ? Formatting.RED : count > 0.5 * cap ? Formatting.GOLD : Formatting.GREEN) : Text.literal("-").formatted(Formatting.GRAY));
            }
        });

        Placeholders.register(new Identifier("world", "mob_count"), (ctx, arg) -> {
            ServerWorld world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = ctx.server().getOverworld();
            }

            SpawnHelper.Info info = world.getChunkManager().getSpawnInfo();

            SpawnGroup spawnGroup = null;
            if (arg != null) {
                spawnGroup = SpawnGroup.valueOf(arg.toUpperCase(Locale.ROOT));
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

        Placeholders.register(new Identifier("world", "mob_cap"), (ctx, arg) -> {
            ServerWorld world;
            if (ctx.player() != null) {
                world = ctx.player().getServerWorld();
            } else {
                world = ctx.server().getOverworld();
            }

            SpawnHelper.Info info = world.getChunkManager().getSpawnInfo();

            SpawnGroup spawnGroup = null;
            if (arg != null) {
                spawnGroup = SpawnGroup.valueOf(arg.toUpperCase(Locale.ROOT));
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
