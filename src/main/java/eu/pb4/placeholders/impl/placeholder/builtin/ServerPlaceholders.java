package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.arguments.StringArgs;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.scoreboard.ScoreboardEntry;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ServerPlaceholders {
    public static void register() {
        Placeholders.register(Identifier.of("server", "tps"), (ctx, arg) -> {
            double tps = TimeUnit.SECONDS.toMillis(1) / Math.max(ctx.server().getAverageTickTime(), ctx.server().getTickManager().getMillisPerTick());
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

        Placeholders.register(Identifier.of("server", "tps_colored"), (ctx, arg) -> {
            double tps = TimeUnit.SECONDS.toMillis(1) / Math.max(ctx.server().getAverageTickTime(), ctx.server().getTickManager().getMillisPerTick());
            String format = "%.1f";

            if (arg != null) {
                try {
                    int x = Integer.parseInt(arg);
                    format = "%." + x + "f";
                } catch (Exception e) {
                    format = "%.1f";
                }
            }
            return PlaceholderResult.value(Text.literal(String.format(format, tps)).formatted(tps > 19 ? Formatting.GREEN : tps > 16 ? Formatting.GOLD : Formatting.RED));
        });

        Placeholders.register(Identifier.of("server", "mspt"), (ctx, arg) -> PlaceholderResult.value(String.format("%.0f", ctx.server().getAverageTickTime())));

        Placeholders.register(Identifier.of("server", "mspt_colored"), (ctx, arg) -> {
            float x = ctx.server().getAverageTickTime();
            return PlaceholderResult.value(Text.literal(String.format("%.0f", x)).formatted(x < 45 ? Formatting.GREEN : x < 51 ? Formatting.GOLD : Formatting.RED));
        });


        Placeholders.register(Identifier.of("server", "time"), (ctx, arg) -> {
            SimpleDateFormat format = new SimpleDateFormat(arg != null ? arg : "HH:mm:ss");
            return PlaceholderResult.value(format.format(new Date(System.currentTimeMillis())));
        });

        Placeholders.register(Identifier.of("server", "time_new"), (ctx, arg) -> {
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

            Placeholders.register(Identifier.of("server", "uptime"), (ctx, arg) -> {
                if (ref.server == null || !ref.server.refersTo(ctx.server())) {
                    ref.server = new WeakReference<>(ctx.server());
                    ref.ms = System.currentTimeMillis() - ctx.server().getTicks() * 50L;
                }

                return PlaceholderResult.value(arg != null
                        ? DurationFormatUtils.formatDuration((System.currentTimeMillis() - ref.ms), arg, true)
                        : GeneralUtils.durationToString((System.currentTimeMillis() - ref.ms) / 1000)
                );
            });
        }

        Placeholders.register(Identifier.of("server", "version"), (ctx, arg) -> PlaceholderResult.value(ctx.server().getVersion()));

        Placeholders.register(Identifier.of("server", "motd"), (ctx, arg) -> {
            var metadata = ctx.server().getServerMetadata();

            if (metadata == null) {
                return PlaceholderResult.invalid("Server metadata missing!");
            }

            return PlaceholderResult.value(metadata.description());
        });

        Placeholders.register(Identifier.of("server", "mod_version"), (ctx, arg) -> {
            if (arg != null) {
                var container = FabricLoader.getInstance().getModContainer(arg);

                if (container.isPresent()) {
                    return PlaceholderResult.value(Text.literal(container.get().getMetadata().getVersion().getFriendlyString()));
                }
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(Identifier.of("server", "mod_name"), (ctx, arg) -> {
            if (arg != null) {
                var container = FabricLoader.getInstance().getModContainer(arg);

                if (container.isPresent()) {
                    return PlaceholderResult.value(Text.literal(container.get().getMetadata().getName()));
                }
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(Identifier.of("server", "brand"), (ctx, arg) -> {
            return PlaceholderResult.value(Text.literal(ctx.server().getServerModName()));
        });

        Placeholders.register(Identifier.of("server", "mod_count"), (ctx, arg) -> {
            return PlaceholderResult.value(Text.literal("" + FabricLoader.getInstance().getAllMods().size()));
        });

        Placeholders.register(Identifier.of("server", "mod_description"), (ctx, arg) -> {
            if (arg != null) {
                var container = FabricLoader.getInstance().getModContainer(arg);

                if (container.isPresent()) {
                    return PlaceholderResult.value(Text.literal(container.get().getMetadata().getDescription()));
                }
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(Identifier.of("server", "name"), (ctx, arg) -> PlaceholderResult.value(ctx.server().getName()));

        Placeholders.register(Identifier.of("server", "used_ram"), (ctx, arg) -> {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

            return PlaceholderResult.value(Objects.equals(arg, "gb")
                    ? String.format("%.1f", (float) heapUsage.getUsed() / 1073741824)
                    : String.format("%d", heapUsage.getUsed() / 1048576));
        });

        Placeholders.register(Identifier.of("server", "max_ram"), (ctx, arg) -> {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

            return PlaceholderResult.value(Objects.equals(arg, "gb")
                    ? String.format("%.1f", (float) heapUsage.getMax() / 1073741824)
                    : String.format("%d", heapUsage.getMax() / 1048576));
        });

        Placeholders.register(Identifier.of("server", "online"), (ctx, arg) -> PlaceholderResult.value(String.valueOf(ctx.server().getPlayerManager().getCurrentPlayerCount())));
        Placeholders.register(Identifier.of("server", "max_players"), (ctx, arg) -> PlaceholderResult.value(String.valueOf(ctx.server().getPlayerManager().getMaxPlayerCount())));

        Placeholders.register(Identifier.of("server", "objective_name_top"), (ctx, arg) -> {
            var args = arg.split(" ");
            if (args.length >= 2) {
                ServerScoreboard scoreboard = ctx.server().getScoreboard();
                ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(args[0]);
                if (scoreboardObjective == null) {
                    return PlaceholderResult.invalid("Invalid objective!");
                }
                try {
                    int position = Integer.parseInt(args[1]);
                    Collection<ScoreboardEntry> scoreboardEntries = scoreboard.getScoreboardEntries(scoreboardObjective);
                    ScoreboardEntry scoreboardEntry = scoreboardEntries.toArray(ScoreboardEntry[]::new)[scoreboardEntries.size() - position];
                    return PlaceholderResult.value(scoreboardEntry.name());
                } catch (Exception e) {
                    /* Into the void you go! */
                    return PlaceholderResult.invalid("Invalid position!");
                }
            }
            return PlaceholderResult.invalid("Not enough arguments!");
        });
        Placeholders.register(Identifier.of("server", "objective_score_top"), (ctx, arg) -> {
            var args = arg.split(" ");
            if (args.length >= 2) {
                ServerScoreboard scoreboard = ctx.server().getScoreboard();
                ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(args[0]);
                if (scoreboardObjective == null) {
                    return PlaceholderResult.invalid("Invalid objective!");
                }
                try {
                    int position = Integer.parseInt(args[1]);
                    Collection<ScoreboardEntry> scoreboardEntries = scoreboard.getScoreboardEntries(scoreboardObjective);
                    ScoreboardEntry scoreboardEntry = scoreboardEntries.toArray(ScoreboardEntry[]::new)[scoreboardEntries.size() - position];
                    return PlaceholderResult.value(String.valueOf(scoreboardEntry.value()));
                } catch (Exception e) {
                    /* Into the void you go! */
                    return PlaceholderResult.invalid("Invalid position!");
                }
            }
            return PlaceholderResult.invalid("Not enough arguments!");
        });
    }
}
