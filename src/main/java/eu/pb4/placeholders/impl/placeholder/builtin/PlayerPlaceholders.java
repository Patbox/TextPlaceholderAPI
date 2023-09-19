package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.time.DurationFormatUtils;


public class PlayerPlaceholders {
    public static void register() {
        Placeholders.register(new Identifier("player", "name"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getName());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "name_visual"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(GeneralUtils.removeHoverAndClick(ctx.player().getName()));
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "name_unformatted"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getName().getString());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "ping"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.valueOf(ctx.player().networkHandler.getLatency()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "ping_colored"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                int x = ctx.player().networkHandler.getLatency();
                return PlaceholderResult.value(Text.literal(String.valueOf(x)).formatted(x < 100 ? Formatting.GREEN : x < 200 ? Formatting.GOLD : Formatting.RED));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "displayname"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getDisplayName());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "display_name"), Placeholders.getPlaceholders().get(new Identifier("player", "displayname")));

        Placeholders.register(new Identifier("player", "displayname_visual"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(GeneralUtils.removeHoverAndClick(ctx.player().getDisplayName()));
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "display_name_visual"), Placeholders.getPlaceholders().get(new Identifier("player", "displayname_visual")));

        Placeholders.register(new Identifier("player", "displayname_unformatted"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(Text.literal(ctx.player().getDisplayName().getString()));
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
        Placeholders.register(new Identifier("player", "display_name_unformatted"), Placeholders.getPlaceholders().get(new Identifier("player", "displayname_unformatted")));

        Placeholders.register(new Identifier("player", "inventory_slot"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    int slot = Integer.parseInt(arg);

                    var inventory = ctx.player().getInventory();

                    if (slot >= 0 && slot < inventory.size()) {
                        var stack = inventory.getStack(slot);

                        return PlaceholderResult.value(GeneralUtils.getItemText(stack, true));
                    }

                } catch (Exception e) {
                    // noop
                }
                return PlaceholderResult.invalid("Invalid argument");
            } else {
                return PlaceholderResult.invalid("No player or invalid argument!");
            }
        });

        Placeholders.register(new Identifier("player", "inventory_slot_no_rarity"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    int slot = Integer.parseInt(arg);

                    var inventory = ctx.player().getInventory();

                    if (slot >= 0 && slot < inventory.size()) {
                        var stack = inventory.getStack(slot);

                        return PlaceholderResult.value(GeneralUtils.getItemText(stack, false));
                    }

                } catch (Exception e) {
                    // noop
                }
                return PlaceholderResult.invalid("Invalid argument");
            } else {
                return PlaceholderResult.invalid("No player or invalid argument!");
            }
        });

        Placeholders.register(new Identifier("player", "equipment_slot"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    var slot = EquipmentSlot.byName(arg);

                    var stack = ctx.player().getEquippedStack(slot);
                    return PlaceholderResult.value(GeneralUtils.getItemText(stack, true));
                } catch (Exception e) {
                    // noop
                }
                return PlaceholderResult.invalid("Invalid argument");
            } else {
                return PlaceholderResult.invalid("No player or invalid argument!");
            }
        });

        Placeholders.register(new Identifier("player", "equipment_slot_no_rarity"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    var slot = EquipmentSlot.byName(arg);

                    var stack = ctx.player().getEquippedStack(slot);
                    return PlaceholderResult.value(GeneralUtils.getItemText(stack, false));
                } catch (Exception e) {
                    // noop
                }
                return PlaceholderResult.invalid("Invalid argument");
            } else {
                return PlaceholderResult.invalid("No player or invalid argument!");
            }
        });

        Placeholders.register(new Identifier("player", "playtime"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                int x = ctx.player().getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME));
                return PlaceholderResult.value(arg != null
                        ? DurationFormatUtils.formatDuration((long) x * 50, arg, true)
                        : GeneralUtils.durationToString((long) x / 20)
                );
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "statistic"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    var args = arg.split(" ");

                    if (args.length == 1) {
                        var identifier = Identifier.tryParse(args[0]);
                        if (identifier != null) {
                            var stat = Stats.CUSTOM.getOrCreateStat(Registries.CUSTOM_STAT.get(identifier));
                            int x = ctx.player().getStatHandler().getStat(stat);
                            return PlaceholderResult.value(stat.format(x));
                        }
                    } else if (args.length >= 2) {
                        var type = Identifier.tryParse(args[0]);
                        var id = Identifier.tryParse(args[1]);
                        if (type != null) {
                            var statType = (StatType<Object>) Registries.STAT_TYPE.get(type);

                            if (statType != null) {
                                var key = statType.getRegistry().get(id);
                                if (key != null) {
                                    var stat = statType.getOrCreateStat(key);
                                    int x = ctx.player().getStatHandler().getStat(stat);
                                    return PlaceholderResult.value(stat.format(x));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    /* Into the void you go! */
                }
                return PlaceholderResult.invalid("Invalid statistic!");
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "statistic_raw"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    var args = arg.split(" ");

                    if (args.length == 1) {
                        var identifier = Identifier.tryParse(args[0]);
                        if (identifier != null) {
                            var stat = Stats.CUSTOM.getOrCreateStat(Registries.CUSTOM_STAT.get(identifier));
                            int x = ctx.player().getStatHandler().getStat(stat);
                            return PlaceholderResult.value(String.valueOf(x));
                        }
                    } else if (args.length >= 2) {
                        var type = Identifier.tryParse(args[0]);
                        var id = Identifier.tryParse(args[1]);
                        if (type != null) {
                            var statType = (StatType<Object>) Registries.STAT_TYPE.get(type);

                            if (statType != null) {
                                var key = statType.getRegistry().get(id);
                                if (key != null) {
                                    var stat = statType.getOrCreateStat(key);
                                    int x = ctx.player().getStatHandler().getStat(stat);
                                    return PlaceholderResult.value(String.valueOf(x));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    /* Into the void you go! */
                }
                return PlaceholderResult.invalid("Invalid statistic!");
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "objective"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    ServerScoreboard scoreboard = ctx.server().getScoreboard();
                    ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(arg);
                    if (scoreboardObjective == null) {
                        return PlaceholderResult.invalid("Invalid objective!");
                    }
                    ScoreboardPlayerScore score = scoreboard.getPlayerScore(ctx.player().getEntityName(), scoreboardObjective);
                    return PlaceholderResult.value(String.valueOf(score.getScore()));
                } catch (Exception e) {
                    /* Into the void you go! */
                }
                return PlaceholderResult.invalid("Invalid objective!");
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "pos_x"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                double value = ctx.player().getX();
                String format = "%.2f";

                if (arg != null) {
                    try {
                        int x = Integer.parseInt(arg);
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

        Placeholders.register(new Identifier("player", "pos_y"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                double value = ctx.player().getY();
                String format = "%.2f";

                if (arg != null) {
                    try {
                        int x = Integer.parseInt(arg);
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

        Placeholders.register(new Identifier("player", "pos_z"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                double value = ctx.player().getZ();
                String format = "%.2f";

                if (arg != null) {
                    try {
                        int x = Integer.parseInt(arg);
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

        Placeholders.register(new Identifier("player", "uuid"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getUuidAsString());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of("" + ctx.gameProfile().getId()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "health"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "max_health"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getMaxHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "hunger"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHungerManager().getFoodLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "saturation"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHungerManager().getSaturationLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "team_name"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                var team = ctx.player().getScoreboardTeam();
                return PlaceholderResult.value(team==null ? Text.empty() : Text.of(team.getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "team_displayname"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                var team = (Team) ctx.player().getScoreboardTeam();
                return PlaceholderResult.value(team==null ? Text.empty() : team.getDisplayName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "team_displayname_formatted"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                var team = (Team) ctx.player().getScoreboardTeam();
                return PlaceholderResult.value(team==null ? Text.empty() : team.getFormattedName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }
}
