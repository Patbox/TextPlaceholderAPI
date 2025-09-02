package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.arguments.SimpleArguments;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.scoreboard.ReadableScoreboardScore;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.text.object.PlayerTextObjectContents;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.dimension.DimensionType;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Locale;


public class PlayerPlaceholders {
    public static void register() {
        Placeholders.register(Identifier.of("player", "name"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getName());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "name_visual"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(GeneralUtils.removeHoverAndClick(ctx.entity().getName()));
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "name_unformatted"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getName().getString());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "ping"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.valueOf(ctx.player().networkHandler.getLatency()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "ping_colored"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                int x = ctx.player().networkHandler.getLatency();
                return PlaceholderResult.value(Text.literal(String.valueOf(x)).formatted(x < 100 ? Formatting.GREEN : x < 200 ? Formatting.GOLD : Formatting.RED));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "displayname"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getDisplayName());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "display_name"), Placeholders.getPlaceholders().get(Identifier.of("player", "displayname")));

        Placeholders.register(Identifier.of("player", "displayname_visual"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(GeneralUtils.removeHoverAndClick(ctx.entity().getDisplayName()));
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "display_name_visual"), Placeholders.getPlaceholders().get(Identifier.of("player", "displayname_visual")));

        Placeholders.register(Identifier.of("player", "displayname_unformatted"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(Text.literal(ctx.entity().getDisplayName().getString()));
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of(ctx.gameProfile().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
        Placeholders.register(Identifier.of("player", "display_name_unformatted"), Placeholders.getPlaceholders().get(Identifier.of("player", "displayname_unformatted")));

        Placeholders.register(Identifier.of("player", "inventory_slot"), (ctx, arg) -> {
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

        Placeholders.register(Identifier.of("player", "inventory_slot_no_rarity"), (ctx, arg) -> {
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

        Placeholders.register(Identifier.of("player", "equipment_slot"), (ctx, arg) -> {
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

        Placeholders.register(Identifier.of("player", "equipment_slot_no_rarity"), (ctx, arg) -> {
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

        Placeholders.register(Identifier.of("player", "playtime"), (ctx, arg) -> {
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

        Placeholders.register(Identifier.of("player", "statistic"), (ctx, arg) -> {
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

        Placeholders.register(Identifier.of("player", "statistic_raw"), (ctx, arg) -> {
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

        Placeholders.register(Identifier.of("player", "objective"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    ServerScoreboard scoreboard = ctx.server().getScoreboard();
                    ScoreboardObjective scoreboardObjective = scoreboard.getNullableObjective(arg);
                    if (scoreboardObjective == null) {
                        return PlaceholderResult.invalid("Invalid objective!");
                    }
                    ReadableScoreboardScore score = scoreboard.getScore(ctx.player(), scoreboardObjective);
                    return PlaceholderResult.value(String.valueOf(score.getScore()));
                } catch (Exception e) {
                    /* Into the void you go! */
                }
                return PlaceholderResult.invalid("Invalid objective!");
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "facing"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getFacing().asString());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "facing_axis"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                var facing = ctx.entity().getFacing();
                return PlaceholderResult.value(
                        (facing.getDirection() == Direction.AxisDirection.NEGATIVE ? "-" : "+") + facing.getAxis().asString().toUpperCase(Locale.ROOT));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "horizontal_facing"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getHorizontalFacing().asString());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "horizontal_facing_axis"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                var facing = ctx.entity().getHorizontalFacing();
                return PlaceholderResult.value(
                        (facing.getDirection() == Direction.AxisDirection.NEGATIVE ? "-" : "+") + facing.getAxis().asString().toUpperCase(Locale.ROOT));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "pos_x"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                double value = ctx.entity().getX();
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

        Placeholders.register(Identifier.of("player", "pos_y"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                double value = ctx.entity().getY();
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

        Placeholders.register(Identifier.of("player", "pos_z"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                double value = ctx.entity().getZ();
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

        Placeholders.register(Identifier.of("player", "pos_x_scaled"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                ServerWorld otherWorld = null;

                if (arg != null) {
                    var worldId = Identifier.tryParse(arg);
                    if (worldId != null) {
                        otherWorld = ctx.server().getWorld(RegistryKey.of(RegistryKeys.WORLD, worldId));
                    }
                }

                if (otherWorld == null) {
                    otherWorld = ctx.server().getOverworld();
                }

                double value = ctx.entity().getX() * DimensionType.getCoordinateScaleFactor(ctx.entity().getEntityWorld().getDimension(), otherWorld.getDimension());                String format = "%.2f";

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

        Placeholders.register(Identifier.of("player", "pos_y_scaled"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                ServerWorld otherWorld = null;

                if (arg != null) {
                    var worldId = Identifier.tryParse(arg);
                    if (worldId != null) {
                        otherWorld = ctx.server().getWorld(RegistryKey.of(RegistryKeys.WORLD, worldId));
                    }
                }

                if (otherWorld == null) {
                    otherWorld = ctx.server().getOverworld();
                }

                double value = ctx.entity().getY() * DimensionType.getCoordinateScaleFactor(ctx.entity().getEntityWorld().getDimension(), otherWorld.getDimension());                String format = "%.2f";

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

        Placeholders.register(Identifier.of("player", "pos_z_scaled"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                ServerWorld otherWorld = null;

                if (arg != null) {
                    var worldId = Identifier.tryParse(arg);
                    if (worldId != null) {
                        otherWorld = ctx.server().getWorld(RegistryKey.of(RegistryKeys.WORLD, worldId));
                    }
                }

                if (otherWorld == null) {
                    otherWorld = ctx.server().getOverworld();
                }

                double value = ctx.entity().getZ() * DimensionType.getCoordinateScaleFactor(ctx.entity().getEntityWorld().getDimension(), otherWorld.getDimension());
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

        Placeholders.register(Identifier.of("player", "uuid"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getUuidAsString());
            } else if (ctx.hasGameProfile()) {
                return PlaceholderResult.value(Text.of("" + ctx.gameProfile().id()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "health"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "max_health"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getMaxHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "hunger"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.valueOf(ctx.player().getHungerManager().getFoodLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "saturation"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHungerManager().getSaturationLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "team_name"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                var team = ctx.player().getScoreboardTeam();
                return PlaceholderResult.value(team==null ? Text.empty() : Text.of(team.getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "team_displayname"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                var team = (Team) ctx.player().getScoreboardTeam();
                return PlaceholderResult.value(team==null ? Text.empty() : team.getDisplayName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "team_displayname_formatted"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                var team = (Team) ctx.player().getScoreboardTeam();
                return PlaceholderResult.value(team==null ? Text.empty() : team.getFormattedName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.register(Identifier.of("player", "biome"), (ctx, arg) -> {
            var world = ctx.entity() != null ? ctx.entity().getEntityWorld() : ctx.source().getWorld();
            var pos = ctx.entity() != null ? ctx.entity().getBlockPos() : BlockPos.ofFloored(ctx.source().getPosition());


            var biome = world.getBiome(pos);
            if (biome.getKey().isEmpty()) {
                return PlaceholderResult.invalid("No biome key??");
            }

            return PlaceholderResult.value(Text.translatable(biome.getKey().get().getValue().toTranslationKey("biome"), biome.getKey().get().getValue().toString()));
        });

        Placeholders.register(Identifier.of("player", "biome_raw"), (ctx, arg) -> {
            var world = ctx.entity() != null ? ctx.entity().getEntityWorld() : ctx.source().getWorld();
            var pos = ctx.entity() != null ? ctx.entity().getBlockPos() : BlockPos.ofFloored(ctx.source().getPosition());


            var biome = world.getBiome(pos);
            if (biome.getKey().isEmpty()) {
                return PlaceholderResult.invalid("No biome key??");
            }

            return PlaceholderResult.value(biome.getKey().get().getValue().toString());
        });

        Placeholders.register(Identifier.of("player", "head"), (ctx, arg) -> {
            if (!ctx.hasGameProfile()) {
                return PlaceholderResult.invalid("No Game Profile!");
            }

            return PlaceholderResult.value(Text.method_74062(new PlayerTextObjectContents(ProfileComponent.ofStatic(ctx.gameProfile()), SimpleArguments.bool(arg, true))));
        });
    }
}
