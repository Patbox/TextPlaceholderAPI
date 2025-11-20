package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;

public class WorldPlaceholders {
    static final int CHUNK_AREA = (int)Math.pow(17.0D, 2.0D);

    public static void register() {
        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "time"), (ctx, arg) -> {
            ServerLevel world;
            if (ctx.player() != null) {
                world = ctx.player().level();
            } else {
                world = ctx.server().overworld();
            }

            long dayTime = (long) (world.getDayTime() * 3.6 / 60);

            return PlaceholderResult.value(String.format("%02d:%02d", (dayTime / 60 + 6) % 24, dayTime % 60));
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "time_alt"), (ctx, arg) -> {
            ServerLevel world;
            if (ctx.player() != null) {
                world = ctx.player().level();
            } else {
                world = ctx.server().overworld();
            }

            long dayTime = (long) (world.getDayTime() * 3.6 / 60);
            long x = (dayTime / 60 + 6) % 24;
            long y = x % 12;
            if (y == 0) {
                y = 12;
            }
            return PlaceholderResult.value(String.format("%02d:%02d %s", y, dayTime % 60, x > 11 ? "PM" : "AM" ));
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "day"), (ctx, arg) -> {
            ServerLevel world;
            if (ctx.player() != null) {
                world = ctx.player().level();
            } else {
                world = ctx.server().overworld();
            }

            return PlaceholderResult.value("" + world.getDayTime() / 24000);
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "id"), (ctx, arg) -> {
            ServerLevel world;
            if (ctx.player() != null) {
                world = ctx.player().level();
            } else {
                world = ctx.server().overworld();
            }

            return PlaceholderResult.value(world.dimension().location().toString());
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "name"), (ctx, arg) -> {
            ServerLevel world;
            if (ctx.player() != null) {
                world = ctx.player().level();
            } else {
                world = ctx.server().overworld();
            }
            List<String> parts = new ArrayList<>();
            {
                String[] words = world.dimension().location().getPath().split("_");
                for (String word : words) {
                    String[] s = word.split("", 2);
                    s[0] = s[0].toUpperCase(Locale.ROOT);
                    parts.add(String.join("", s));
                }
            }
            return PlaceholderResult.value(String.join(" ", parts));
        });



        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "player_count"), (ctx, arg) -> {
            ServerLevel world;
            if (ctx.player() != null) {
                world = ctx.player().level();
            } else {
                world = ctx.server().overworld();
            }

            return PlaceholderResult.value("" + world.players().size());
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "mob_count_colored"), (ctx, arg) -> {
            ServerLevel world;
            if (ctx.player() != null) {
                world = ctx.player().level();
            } else {
                world = ctx.server().overworld();
            }

            NaturalSpawner.SpawnState info = world.getChunkSource().getLastSpawnState();

            MobCategory spawnGroup = null;
            if (arg != null) {
                spawnGroup = MobCategory.valueOf(arg.toUpperCase(Locale.ROOT));
            }

            if (spawnGroup != null) {
                int count = info.getMobCategoryCounts().getInt(spawnGroup);
                int cap = spawnGroup.getMaxInstancesPerChunk() * info.getSpawnableChunkCount() / CHUNK_AREA;

                return PlaceholderResult.value(count > 0 ? Component.literal("" + count).withStyle(count > cap ? ChatFormatting.LIGHT_PURPLE : count > 0.8 * cap ? ChatFormatting.RED : count > 0.5 * cap ? ChatFormatting.GOLD : ChatFormatting.GREEN) : Component.literal("-").withStyle(ChatFormatting.GRAY));
            } else {
                int cap = 0;

                for (MobCategory group : MobCategory.values()) {
                    cap += group.getMaxInstancesPerChunk();
                }
                cap = cap * info.getSpawnableChunkCount() / CHUNK_AREA;

                int count = 0;

                for (int value : info.getMobCategoryCounts().values()) {
                    count += value;
                }
                return PlaceholderResult.value(count > 0 ? Component.literal("" + count).withStyle(count > cap ? ChatFormatting.LIGHT_PURPLE : count > 0.8 * cap ? ChatFormatting.RED : count > 0.5 * cap ? ChatFormatting.GOLD : ChatFormatting.GREEN) : Component.literal("-").withStyle(ChatFormatting.GRAY));
            }
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "mob_count"), (ctx, arg) -> {
            ServerLevel world;
            if (ctx.player() != null) {
                world = ctx.player().level();
            } else {
                world = ctx.server().overworld();
            }

            NaturalSpawner.SpawnState info = world.getChunkSource().getLastSpawnState();

            MobCategory spawnGroup = null;
            if (arg != null) {
                spawnGroup = MobCategory.valueOf(arg.toUpperCase(Locale.ROOT));
            }

            if (spawnGroup != null) {
                return PlaceholderResult.value("" + info.getMobCategoryCounts().getInt(spawnGroup));
            } else {
                int x = 0;

                for (int value : info.getMobCategoryCounts().values()) {
                    x += value;
                }
                return PlaceholderResult.value("" + x);
            }
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "mob_cap"), (ctx, arg) -> {
            ServerLevel world;
            if (ctx.player() != null) {
                world = ctx.player().level();
            } else {
                world = ctx.server().overworld();
            }

            NaturalSpawner.SpawnState info = world.getChunkSource().getLastSpawnState();

            MobCategory spawnGroup = null;
            if (arg != null) {
                spawnGroup = MobCategory.valueOf(arg.toUpperCase(Locale.ROOT));
            }

            if (spawnGroup != null) {
                return PlaceholderResult.value("" + spawnGroup.getMaxInstancesPerChunk() * info.getSpawnableChunkCount() / CHUNK_AREA);
            } else {
                int x = 0;

                for (MobCategory group : MobCategory.values()) {
                    x += group.getMaxInstancesPerChunk();
                }
                return PlaceholderResult.value("" + x * info.getSpawnableChunkCount() / CHUNK_AREA);
            }
        });

        Placeholders.register(ResourceLocation.fromNamespaceAndPath("world", "weather"), (ctx, arg) -> {
            Level world;
            if (ctx.entity() != null) {
                world = ctx.entity().level();
            } else {
                world = ctx.source().getLevel();
            }

           return PlaceholderResult.value(world.isThundering() ? "rain & thunder" : world.isRaining() ? "rain" : "clear");
        });
    }
}
