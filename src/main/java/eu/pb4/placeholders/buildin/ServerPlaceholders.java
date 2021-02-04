package eu.pb4.placeholders.buildin;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderResult;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

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

                }
            }
            return PlaceholderResult.value(new LiteralText(String.format(format, tps)).formatted(tps > 19 ? Formatting.GREEN : tps > 16 ? Formatting.GOLD : Formatting.RED));
        });

        PlaceholderAPI.register(new Identifier("server", "mspt"), (ctx) -> PlaceholderResult.value(String.valueOf(ctx.getServer().getTickTime())));

        PlaceholderAPI.register(new Identifier("server", "mspt_colored"), (ctx) -> {
            float x = ctx.getServer().getTickTime();
            return PlaceholderResult.value(new LiteralText(String.valueOf(x)).formatted(x < 45 ? Formatting.GREEN : x < 51 ? Formatting.GOLD : Formatting.RED));
        });


        PlaceholderAPI.register(new Identifier("server", "time"), (ctx) -> {
            SimpleDateFormat format = new SimpleDateFormat(ctx.getArgument());
            return PlaceholderResult.value(format.format(new Date(System.currentTimeMillis())));
        });

        PlaceholderAPI.register(new Identifier("server", "version"), (ctx) -> PlaceholderResult.value(ctx.getServer().getVersion()));
        PlaceholderAPI.register(new Identifier("server", "name"), (ctx) -> PlaceholderResult.value(ctx.getServer().getName()));

        PlaceholderAPI.register(new Identifier("server", "used_ram"), (ctx) -> PlaceholderResult.value(ctx.getArgument().equals("gb")
                    ? String.format("%.1f", (float) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1073741824)
                    : String.format("%d", (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576 ))
        );

        PlaceholderAPI.register(new Identifier("server", "max_ram"), (ctx) -> PlaceholderResult.value(ctx.getArgument().equals("gb")
                ? String.format("%.1f", (float) Runtime.getRuntime().totalMemory() / 1073741824)
                : String.format("%d", Runtime.getRuntime().totalMemory() / 1048576 ))
        );

        PlaceholderAPI.register(new Identifier("server", "online"), (ctx) -> PlaceholderResult.value(String.valueOf(ctx.getServer().getPlayerManager().getCurrentPlayerCount())));
        PlaceholderAPI.register(new Identifier("server", "max_players"), (ctx) -> PlaceholderResult.value(String.valueOf(ctx.getServer().getPlayerManager().getMaxPlayerCount())));
    }
}
