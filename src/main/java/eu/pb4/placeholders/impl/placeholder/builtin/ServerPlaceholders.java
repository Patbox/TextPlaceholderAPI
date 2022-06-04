package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.PlaceholderResult;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ServerPlaceholders {
    public static void register() {
        Placeholders.register(new Identifier("server", "tps"), (ctx, arg) -> {
            float tps = 1000/Math.max(ctx.server().getTickTime(), 50);
            String format = "%.1f";

            if (arg != null) {
                try {
                    int x = Integer.getInteger(arg);
                    format = "%." + x + "f";
                } catch (Exception e) {
                    format = "%.1f";
                }
            }

            return PlaceholderResult.value(String.format(format, tps));
        });

        Placeholders.register(new Identifier("server", "tps_colored"), (ctx, arg) -> {
            float tps = 1000/Math.max(ctx.server().getTickTime(), 50);
            String format = "%.1f";

            if (arg != null) {
                try {
                    int x = Integer.getInteger(arg);
                    format = "%." + x + "f";
                } catch (Exception e) {
                    format = "%.1f";
                }
            }
            return PlaceholderResult.value(Text.literal(String.format(format, tps)).formatted(tps > 19 ? Formatting.GREEN : tps > 16 ? Formatting.GOLD : Formatting.RED));
        });

        Placeholders.register(new Identifier("server", "mspt"), (ctx, arg) -> PlaceholderResult.value(String.format("%.0f", ctx.server().getTickTime())));

        Placeholders.register(new Identifier("server", "mspt_colored"), (ctx, arg) -> {
            float x = ctx.server().getTickTime();
            return PlaceholderResult.value(Text.literal(String.format("%.0f", x)).formatted(x < 45 ? Formatting.GREEN : x < 51 ? Formatting.GOLD : Formatting.RED));
        });


        Placeholders.register(new Identifier("server", "time"), (ctx, arg) -> {
            SimpleDateFormat format = new SimpleDateFormat(arg != null ? arg : "HH:mm:ss");
            return PlaceholderResult.value(format.format(new Date(System.currentTimeMillis())));
        });

        Placeholders.register(new Identifier("server", "version"), (ctx, arg) -> PlaceholderResult.value(ctx.server().getVersion()));

        Placeholders.register(new Identifier("server", "mod_version"), (ctx, arg) -> {
            if (arg != null) {
                var container = FabricLoader.getInstance().getModContainer(arg);

                if (container.isPresent()) {
                    return PlaceholderResult.value(Text.literal(container.get().getMetadata().getVersion().getFriendlyString()));
                }
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(new Identifier("server", "mod_name"), (ctx, arg) -> {
            if (arg != null) {
                var container = FabricLoader.getInstance().getModContainer(arg);

                if (container.isPresent()) {
                    return PlaceholderResult.value(Text.literal(container.get().getMetadata().getName()));
                }
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(new Identifier("server", "mod_description"), (ctx, arg) -> {
            if (arg != null) {
                var container = FabricLoader.getInstance().getModContainer(arg);

                if (container.isPresent()) {
                    return PlaceholderResult.value(Text.literal(container.get().getMetadata().getDescription()));
                }
            }
            return PlaceholderResult.invalid("Invalid argument");
        });

        Placeholders.register(new Identifier("server", "name"), (ctx, arg) -> PlaceholderResult.value(ctx.server().getName()));

        Placeholders.register(new Identifier("server", "used_ram"), (ctx, arg) -> {
            MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
            MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

            return PlaceholderResult.value(arg.equals("gb")
                    ? String.format("%.1f", (float) heapUsage.getUsed() / 1073741824)
                    : String.format("%d", heapUsage.getUsed() / 1048576));
            });

        Placeholders.register(new Identifier("server", "max_ram"), (ctx, arg) -> {
                    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                    MemoryUsage heapUsage = memoryMXBean.getHeapMemoryUsage();

                    return PlaceholderResult.value(arg.equals("gb")
                            ? String.format("%.1f", (float) heapUsage.getMax() / 1073741824)
                            : String.format("%d", heapUsage.getMax() / 1048576));
                });

        Placeholders.register(new Identifier("server", "online"), (ctx, arg) -> PlaceholderResult.value(String.valueOf(ctx.server().getPlayerManager().getCurrentPlayerCount())));
        Placeholders.register(new Identifier("server", "max_players"), (ctx, arg) -> PlaceholderResult.value(String.valueOf(ctx.server().getPlayerManager().getMaxPlayerCount())));
    }
}
