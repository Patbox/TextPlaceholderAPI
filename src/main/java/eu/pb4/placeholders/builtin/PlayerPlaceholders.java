package eu.pb4.placeholders.builtin;

import eu.pb4.placeholders.PlaceholderAPI;
import eu.pb4.placeholders.PlaceholderResult;
import eu.pb4.placeholders.util.GeneralUtils;
import eu.pb4.placeholders.util.PlaceholderUtils;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.time.DurationFormatUtils;


public class PlayerPlaceholders {
    public static void register() {
        PlaceholderAPI.register(new Identifier("player", "name"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.getPlayer().getName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "name_unformatted"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(GeneralUtils.textToString(ctx.getPlayer().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "ping"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.valueOf(ctx.getPlayer().pingMilliseconds));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "ping_colored"), (ctx) -> {
            if (ctx.hasPlayer()) {
                int x = ctx.getPlayer().pingMilliseconds;
                return PlaceholderResult.value(new LiteralText(String.valueOf(x)).formatted(x < 100 ? Formatting.GREEN : x < 200 ? Formatting.GOLD : Formatting.RED));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "displayname"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.getPlayer().getDisplayName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "displayname_unformatted"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(GeneralUtils.textToString(ctx.getPlayer().getDisplayName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "playtime"), (ctx) -> {
            if (ctx.hasPlayer()) {
                int x = ctx.getPlayer().getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_ONE_MINUTE));
                return PlaceholderResult.value(ctx.hasArgument()
                        ? DurationFormatUtils.formatDuration((long) x * 50, ctx.getArgument(), true)
                        : GeneralUtils.durationToString((long) x / 20)
                        );
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "statistic"), (ctx) -> {
            if (ctx.hasPlayer() && ctx.hasArgument()) {
                try {
                    Identifier identifier = Identifier.tryParse(ctx.getArgument());
                    if (identifier != null) {
                        int x = ctx.getPlayer().getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(identifier));
                        return PlaceholderResult.value(String.valueOf(x));
                    }
                } catch (Exception e) {
                    /* Into the void you go! */
                }
                return PlaceholderResult.invalid("Invalid statistic!");
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "pos_x"), (ctx) -> {
            if (ctx.hasPlayer()) {
                double value = ctx.getPlayer().getX();
                String format = "%.2f";

                if (ctx.hasArgument()) {
                    try {
                        int x = Integer.getInteger(ctx.getArgument());
                        format = "%." + x + "f";
                    } catch (Exception e) {
                        format = "%.2f";
                    }
                }

                return PlaceholderResult.value(String.format(format, value));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "pos_y"), (ctx) -> {
            if (ctx.hasPlayer()) {
                double value = ctx.getPlayer().getY();
                String format = "%.2f";

                if (ctx.hasArgument()) {
                    try {
                        int x = Integer.getInteger(ctx.getArgument());
                        format = "%." + x + "f";
                    } catch (Exception e) {
                        format = "%.2f";
                    }
                }

                return PlaceholderResult.value(String.format(format, value));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "pos_z"), (ctx) -> {
            if (ctx.hasPlayer()) {
                double value = ctx.getPlayer().getZ();
                String format = "%.2f";

                if (ctx.hasArgument()) {
                    try {
                        int x = Integer.getInteger(ctx.getArgument());
                        format = "%." + x + "f";
                    } catch (Exception e) {
                        format = "%.2f";
                    }
                }

                return PlaceholderResult.value(String.format(format, value));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "uuid"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.getPlayer().getUuidAsString());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "health"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.getPlayer().getHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "max_health"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.getPlayer().getMaxHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        PlaceholderAPI.register(new Identifier("player", "saturation"), (ctx) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.getPlayer().getHungerManager().getSaturationLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }
}
