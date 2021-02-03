package eu.pb4.placeholders.buildin;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderResult;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.time.DurationFormatUtils;


public class PlayerPlaceholders {
    public static void register() {
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

        PlaceholderAPI.register(new Identifier("player", "playtime"), (ctx) -> {
            if (ctx.playerExist()) {
                int x = ctx.getPlayer().getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_ONE_MINUTE));
                return PlaceholderResult.value(DurationFormatUtils.formatDuration(x * 50, ctx.getArgument().length() == 0 ? "H:mm:ss" : ctx.getArgument(), true));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "statistic"), (ctx) -> {
            if (ctx.playerExist()) {
                Identifier identifier = Identifier.tryParse(ctx.getArgument());
                if (identifier == null || ctx.getArgument().length() == 0) {
                    return PlaceholderResult.invalid("Invalid statistic!");
                }
                return PlaceholderResult.value(String.valueOf(ctx.getPlayer().getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(identifier))));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }
}
