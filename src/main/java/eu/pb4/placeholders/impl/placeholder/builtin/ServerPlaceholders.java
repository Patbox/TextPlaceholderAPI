package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ServerPlaceholders {
    public static void register() {
        Placeholders.register(Identifier.fromNamespaceAndPath("server", "tps"), (ctx, arg) -> {
            double tps = TimeUnit.SECONDS.toMillis(1) / Math.max(ctx.server().getCurrentSmoothedTickTime(), ctx.server().tickRateManager().millisecondsPerTick());
            String format = "%.1f";

            if (arg != null) {
                try {
                    int x = Integer.parseInt(arg);
                    format = "%." + x + "f";
                } catch (Exception e) {
                    format = "%.1f";
                }
            }

            return PlaceholderResult.value(String.format(format, tps));
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "tps_colored"), (ctx, arg) -> {
            double tps = TimeUnit.SECONDS.toMillis(1) / Math.max(ctx.server().getCurrentSmoothedTickTime(), ctx.server().tickRateManager().millisecondsPerTick());
            String format = "%.1f";

            if (arg != null) {
                try {
                    int x = Integer.parseInt(arg);
                    format = "%." + x + "f";
                } catch (Exception e) {
                    format = "%.1f";
                }
            }
            return PlaceholderResult.value(Component.literal(String.format(format, tps)).withStyle(tps > 19 ? ChatFormatting.GREEN : tps > 16 ? ChatFormatting.GOLD : ChatFormatting.RED));
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "mspt"), (ctx, arg) -> PlaceholderResult.value(String.format("%.0f", ctx.server().getCurrentSmoothedTickTime())));

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "mspt_colored"), (ctx, arg) -> {
            float x = ctx.server().getCurrentSmoothedTickTime();
            return PlaceholderResult.value(Component.literal(String.format("%.0f", x)).withStyle(x < 45 ? ChatFormatting.GREEN : x < 51 ? ChatFormatting.GOLD : ChatFormatting.RED));
        });


        Placeholders.register(Identifier.fromNamespaceAndPath("server", "time"), (ctx, arg) -> {
            SimpleDateFormat format = new SimpleDateFormat(arg != null ? arg : "HH:mm:ss");
            return PlaceholderResult.value(format.format(new Date(System.currentTimeMillis())));
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "time_new"), (ctx, arg) -> {
            var args = arg == null ? StringArgs.empty() : StringArgs.full(arg, ' ', ':');
            var format = DateTimeFormatter.ofPattern(args.get("format", "HH:mm:ss"));
            var date = args.get("zone") != null ? LocalDateTime.now(ZoneId.of(args.get("zone", ""))) : LocalDateTime.now();
            return PlaceholderResult.value(format.format(date));
        });

        {
            var ref = new Object() {
                WeakReference<MinecraftServer> server;
                long ms;
            };

            Placeholders.register(Identifier.fromNamespaceAndPath("server", "uptime"), (ctx, arg) -> {
                if (ref.server == null || !ref.server.refersTo(ctx.server())) {
                    ref.server = new WeakReference<>(ctx.server());
                    ref.ms = System.currentTimeMillis() - ctx.server().getTickCount() * 50L;
                }

                return PlaceholderResult.value(arg != null
                        ? DurationFormatUtils.formatDuration((System.currentTimeMillis() - ref.ms), arg, true)
                        : GeneralUtils.durationToString((System.currentTimeMillis() - ref.ms) / 1000)
                );
            });
        }

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "version"), (ctx, arg) -> PlaceholderResult.value(ctx.server().getServerVersion()));

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "motd"), (ctx, arg) -> {
            var metadata = ctx.server().getStatus();

            if (metadata == null) {
                return PlaceholderResult.invalid("Server metadata missing!");
            }

            return PlaceholderResult.value(metadata.description());
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "mod_version"), (ctx, arg) -> {
            if (arg != null) {
                var container = FabricLoader.getInstance().getModContainer(arg);

                if (container.isPresent()) {
                    return PlaceholderResult.value(Component.literal(container.get().getMetadata().getVersion().getFriendlyString()));
                }
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "mod_name"), (ctx, arg) -> {
            if (arg != null) {
                var container = FabricLoader.getInstance().getModContainer(arg);

                if (container.isPresent()) {
                    return PlaceholderResult.value(Component.literal(container.get().getMetadata().getName()));
                }
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "brand"), (ctx, arg) -> {
            return PlaceholderResult.value(Component.literal(ctx.server().getServerModName()));
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "mod_count"), (ctx, arg) -> {
            return PlaceholderResult.value(Component.literal("" + FabricLoader.getInstance().getAllMods().size()));
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "mod_description"), (ctx, arg) -> {
            if (arg != null) {
                var container = FabricLoader.getInstance().getModContainer(arg);

                if (container.isPresent()) {
                    return PlaceholderResult.value(Component.literal(container.get().getMetadata().getDescription()));
                }
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "name"), (ctx, arg) -> PlaceholderResult.value(ctx.server().name()));

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "used_ram"), (ctx, arg) -> {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

            return PlaceholderResult.value(Objects.equals(arg, "gb")
                    ? String.format("%.1f", (float) heapUsage.getUsed() / 1073741824)
                    : String.format("%d", heapUsage.getUsed() / 1048576));
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "max_ram"), (ctx, arg) -> {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

            return PlaceholderResult.value(Objects.equals(arg, "gb")
                    ? String.format("%.1f", (float) heapUsage.getMax() / 1073741824)
                    : String.format("%d", heapUsage.getMax() / 1048576));
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "online"), (ctx, arg) -> PlaceholderResult.value(String.valueOf(ctx.server().getPlayerList().getPlayerCount())));
        Placeholders.register(Identifier.fromNamespaceAndPath("server", "max_players"), (ctx, arg) -> PlaceholderResult.value(String.valueOf(ctx.server().getPlayerList().getMaxPlayers())));

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "objective_name_top"), (ctx, arg) -> {
            var args = arg.split(" ");
            if (args.length >= 2) {
                ServerScoreboard scoreboard = ctx.server().getScoreboard();
                Objective scoreboardObjective = scoreboard.getObjective(args[0]);
                if (scoreboardObjective == null) {
                    return PlaceholderResult.invalid("Invalid objective!");
                }
                try {
                    int position = Integer.parseInt(args[1]);
                    List<PlayerScoreEntry> scoreboardEntries = new ArrayList<>(scoreboard.listPlayerScores(scoreboardObjective));
                    scoreboardEntries.sort(Comparator.comparingInt(PlayerScoreEntry::value).reversed());

                    PlayerScoreEntry scoreboardEntry = scoreboardEntries.get(position - 1);
                    return PlaceholderResult.value(scoreboardEntry.ownerName());
                } catch (Exception e) {
                    /* Into the void you go! */
                    return PlaceholderResult.invalid("Invalid position!");
                }
            }
            return PlaceholderResult.invalid("Not enough arguments!");
        });
        Placeholders.register(Identifier.fromNamespaceAndPath("server", "objective_score_top"), (ctx, arg) -> {
            var args = arg.split(" ");
            if (args.length >= 2) {
                ServerScoreboard scoreboard = ctx.server().getScoreboard();
                Objective scoreboardObjective = scoreboard.getObjective(args[0]);
                if (scoreboardObjective == null) {
                    return PlaceholderResult.invalid("Invalid objective!");
                }
                try {
                    int position = Integer.parseInt(args[1]);
                    List<PlayerScoreEntry> scoreboardEntries = new ArrayList<>(scoreboard.listPlayerScores(scoreboardObjective));
                    scoreboardEntries.sort(Comparator.comparingInt(PlayerScoreEntry::value).reversed());

                    PlayerScoreEntry scoreboardEntry = scoreboardEntries.get(position - 1);
                    return PlaceholderResult.value(String.valueOf(scoreboardEntry.value()));
                } catch (Exception e) {
                    /* Into the void you go! */
                    return PlaceholderResult.invalid("Invalid position!");
                }
            }
            return PlaceholderResult.invalid("Not enough arguments!");
        });

        Placeholders.register(Identifier.fromNamespaceAndPath("server", "objective_score_player"), (ctx, arg) -> {
            var args = arg.split(" ");
            if (args.length >= 2) {
                ServerScoreboard scoreboard = ctx.server().getScoreboard();
                Objective scoreboardObjective = scoreboard.getObjective(args[0]);
                if (scoreboardObjective == null) {
                    return PlaceholderResult.invalid("Invalid Objective!");
                }
                try {
                    Collection<PlayerScoreEntry> scoreboardEntries = scoreboard.listPlayerScores(scoreboardObjective);
                    PlayerScoreEntry entry = scoreboardEntries.stream().filter(scoreboardEntry -> scoreboardEntry.ownerName().getString().equals(args[1])).toList().getFirst();

                    return PlaceholderResult.value(String.valueOf(entry.value()));
                } catch (Exception e) {
                    return PlaceholderResult.invalid("Player Not Found!");
                }
            }
            return PlaceholderResult.invalid("Not enough arguments!");
        });
    }
}
