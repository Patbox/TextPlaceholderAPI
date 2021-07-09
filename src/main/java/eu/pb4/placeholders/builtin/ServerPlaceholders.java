package eu.pb4.placeholders.builtin;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderResult;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerPlaceholders {
    public static void register() {
        PlaceholderAPI.register(new Identifier("server", "tps"), (ctx) -> {
            float tps = 1000/Math.max(ctx.getServer().getTickTime(), 50);
            String format = "%.1f";

            if (ctx.hasArgument()) {
                try {
                    int x = Integer.getInteger(ctx.getArgument());
                    format = "%." + x + "f";
                } catch (Exception e) {
                    format = "%.1f";
                }
            }

            return PlaceholderResult.value(String.format(format, tps));
        });

        PlaceholderAPI.register(new Identifier("server", "tps_colored"), (ctx) -> {
            float tps = 1000/Math.max(ctx.getServer().getTickTime(), 50);
            String format = "%.1f";

            if (ctx.hasArgument()) {
                try {
                    int x = Integer.getInteger(ctx.getArgument());
                    format = "%." + x + "f";
                } catch (Exception e) {
                    format = "%.1f";
                }
            }
            return PlaceholderResult.value(new LiteralText(String.format(format, tps)).formatted(tps > 19 ? Formatting.GREEN : tps > 16 ? Formatting.GOLD : Formatting.RED));
        });

        PlaceholderAPI.register(new Identifier("server", "mspt"), (ctx) -> PlaceholderResult.value(String.format("%.0f", ctx.getServer().getTickTime())));

        PlaceholderAPI.register(new Identifier("server", "mspt_colored"), (ctx) -> {
            float x = ctx.getServer().getTickTime();
            return PlaceholderResult.value(new LiteralText(String.format("%.0f", x)).formatted(x < 45 ? Formatting.GREEN : x < 51 ? Formatting.GOLD : Formatting.RED));
        });


        PlaceholderAPI.register(new Identifier("server", "time"), (ctx) -> {
            SimpleDateFormat format = new SimpleDateFormat(ctx.hasArgument() ? ctx.getArgument() : "HH:mm:ss");
            return PlaceholderResult.value(format.format(new Date(System.currentTimeMillis())));
        });

        PlaceholderAPI.register(new Identifier("server", "version"), (ctx) -> PlaceholderResult.value(ctx.getServer().getVersion()));
        PlaceholderAPI.register(new Identifier("server", "name"), (ctx) -> PlaceholderResult.value(ctx.getServer().getName()));

        PlaceholderAPI.register(new Identifier("server", "used_ram"), (ctx) -> {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

            return PlaceholderResult.value(ctx.getArgument().equals("gb")
                    ? String.format("%.1f", (float) heapUsage.getUsed() / 1073741824)
                    : String.format("%d", heapUsage.getUsed() / 1048576));
            });

        PlaceholderAPI.register(new Identifier("server", "max_ram"), (ctx) -> {
                    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                    MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

                    return PlaceholderResult.value(ctx.getArgument().equals("gb")
                            ? String.format("%.1f", (float) heapUsage.getMax() / 1073741824)
                            : String.format("%d", heapUsage.getMax() / 1048576));
                });

        PlaceholderAPI.register(new Identifier("server", "online"), (ctx) -> PlaceholderResult.value(String.valueOf(ctx.getServer().getPlayerManager().getCurrentPlayerCount())));
        PlaceholderAPI.register(new Identifier("server", "max_players"), (ctx) -> PlaceholderResult.value(String.valueOf(ctx.getServer().getPlayerManager().getMaxPlayerCount())));
    }
}
