package eu.pb4.placeholders;

import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BuildInPlaceholders {
    public static void register() {
        PlaceholderAPI.register(new Identifier("server", "tps"), (ctx) -> {
            float tps = 1000/Math.max(ctx.getServer().getTickTime(), 50);
            return PlaceholderResult.value(String.format("%.2f", tps));
        });

        PlaceholderAPI.register(new Identifier("server", "tps_colored"), (ctx) -> {
            float tps = 1000/Math.max(ctx.getServer().getTickTime(), 50);
            return PlaceholderResult.value(new LiteralText(String.format("%.2f", tps)).formatted(tps > 19 ? Formatting.GREEN : tps > 16 ? Formatting.GOLD : Formatting.RED));
        });

        PlaceholderAPI.register(new Identifier("server", "time"), (ctx) -> {
            SimpleDateFormat format = new SimpleDateFormat(ctx.getArgument());
            return PlaceholderResult.value(format.format(new Date(System.currentTimeMillis())));
        });


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

        PlaceholderAPI.register(new Identifier("player", "name"), (ctx) -> {
            if (ctx.playerExist()) {
                return PlaceholderResult.value(ctx.getPlayer().getName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "ping"), (ctx) -> {
            if (ctx.playerExist()) {
                return PlaceholderResult.value(String.valueOf(ctx.getPlayer().pingMilliseconds));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "displayname"), (ctx) -> {
            if (ctx.playerExist()) {
                return PlaceholderResult.value(ctx.getPlayer().getDisplayName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }
}
