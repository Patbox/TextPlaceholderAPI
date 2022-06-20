package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.time.DurationFormatUtils;


public class PlayerPlaceholders {
    public static void register() {
        Placeholders.register(new Identifier("player", "name"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(ctx.player().getName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "name_visual"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(GeneralUtils.removeHoverAndClick(ctx.player().getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "name_unformatted"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(ctx.player().getName().getString());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "ping"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(String.valueOf(ctx.player().pingMilliseconds));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "ping_colored"), (ctx, arg) -> {
            if (ctx.player() != null) {
                int x = ctx.player().pingMilliseconds;
                return PlaceholderResult.value(Text.literal(String.valueOf(x)).formatted(x < 100 ? Formatting.GREEN : x < 200 ? Formatting.GOLD : Formatting.RED));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "displayname"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(ctx.player().getDisplayName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "displayname_visual"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(GeneralUtils.removeHoverAndClick(ctx.player().getDisplayName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "displayname_unformatted"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(ctx.player().getDisplayName().getString());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "inventory_slot"), (ctx, arg) -> {
            if (ctx.player() != null && arg != null) {
                try {
                    int slot = Integer.parseInt(arg);

                    var inventory = ctx.player().getInventory();

                    if (slot >= 0 && slot < inventory.size()) {
                        var stack = inventory.getStack(slot);

                        return PlaceholderResult.value(GeneralUtils.getItemText(stack));
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
            if (ctx.player() != null && arg != null) {
                try {
                    var slot = EquipmentSlot.byName(arg);

                    var stack = ctx.player().getEquippedStack(slot);
                    return PlaceholderResult.value(GeneralUtils.getItemText(stack));
                } catch (Exception e) {
                    // noop
                }
                return PlaceholderResult.invalid("Invalid argument");
            } else {
                return PlaceholderResult.invalid("No player or invalid argument!");
            }
        });

        Placeholders.register(new Identifier("player", "playtime"), (ctx, arg) -> {
            if (ctx.player() != null) {
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
            if (ctx.player() != null && arg != null) {
                try {
                    Identifier identifier = Identifier.tryParse(arg);
                    if (identifier != null) {
                        int x = ctx.player().getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Registry.CUSTOM_STAT.get(identifier)));
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

        Placeholders.register(new Identifier("player", "pos_x"), (ctx, arg) -> {
            if (ctx.player() != null) {
                double value = ctx.player().getX();
                String format = "%.2f";

                if (arg != null) {
                    try {
                        int x = Integer.getInteger(arg);
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
            if (ctx.player() != null) {
                double value = ctx.player().getY();
                String format = "%.2f";

                if (arg != null) {
                    try {
                        int x = Integer.getInteger(arg);
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
            if (ctx.player() != null) {
                double value = ctx.player().getZ();
                String format = "%.2f";

                if (arg != null) {
                    try {
                        int x = Integer.getInteger(arg);
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
            if (ctx.player() != null) {
                return PlaceholderResult.value(ctx.player().getUuidAsString());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "health"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "max_health"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getMaxHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "hunger"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHungerManager().getFoodLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(new Identifier("player", "saturation"), (ctx, arg) -> {
            if (ctx.player() != null) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHungerManager().getSaturationLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
    }
}
