package eu.pb4.placeholders.builtin;

import eu.pb4.placeholders.Helpers;
import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderResult;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
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

        PlaceholderAPI.register(new Identifier("player", "name_unformatted"), (ctx) -> {
            if (ctx.playerExist()) {
                return PlaceholderResult.value(Helpers.textToString(ctx.getPlayer().getName()));
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

        PlaceholderAPI.register(new Identifier("player", "ping_colored"), (ctx) -> {
            if (ctx.playerExist()) {
                int x = ctx.getPlayer().pingMilliseconds;
                return PlaceholderResult.value(new LiteralText(String.valueOf(x)).formatted(x < 100 ? Formatting.GREEN : x < 200 ? Formatting.GOLD : Formatting.RED));
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

        PlaceholderAPI.register(new Identifier("player", "displayname_unformatted"), (ctx) -> {
            if (ctx.playerExist()) {
                return PlaceholderResult.value(Helpers.textToString(ctx.getPlayer().getDisplayName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "playtime"), (ctx) -> {
            if (ctx.playerExist()) {
                int x = ctx.getPlayer().getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_ONE_MINUTE));
                return PlaceholderResult.value(ctx.hasArgument()
                        ? DurationFormatUtils.formatDuration((long) x * 50, ctx.getArgument(), true)
                        : Helpers.durationToString((long) x / 20)
                        );
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "statistic"), (ctx) -> {
            if (ctx.playerExist()) {
                Identifier identifier = Identifier.tryParse(ctx.getArgument());
                if (identifier != null && ctx.hasArgument()) {
                    return PlaceholderResult.value(String.valueOf(ctx.getPlayer().getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(identifier))));
                }
                return PlaceholderResult.invalid("Invalid statistic!");
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }
}
