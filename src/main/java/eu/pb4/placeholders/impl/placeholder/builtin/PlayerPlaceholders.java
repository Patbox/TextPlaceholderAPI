package eu.pb4.placeholders.impl.placeholder.builtin;

import eu.pb4.placeholders.api.PlaceholderResult;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.arguments.SimpleArguments;
import eu.pb4.placeholders.impl.GeneralUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.objects.PlayerSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.util.ARGB;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.waypoints.WaypointTransmitter;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.util.Locale;


public class PlayerPlaceholders {
    public static void register() {
        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "name"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getName());
            } else if (ctx.hasNameAndId()) {
                return PlaceholderResult.value(Component.nullToEmpty(ctx.nameAndId().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "name_visual"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(GeneralUtils.removeHoverAndClick(ctx.entity().getName()));
            } else if (ctx.hasNameAndId()) {
                return PlaceholderResult.value(Component.nullToEmpty(ctx.nameAndId().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "name_unformatted"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getName().getString());
            } else if (ctx.hasNameAndId()) {
                return PlaceholderResult.value(Component.nullToEmpty(ctx.nameAndId().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerServer(Identifier.fromNamespaceAndPath("player", "ping"), (ctx, arg) -> {
            if (ctx.hasServerPlayer()) {
                return PlaceholderResult.value(String.valueOf(ctx.serverPlayer().connection.latency()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerServer(Identifier.fromNamespaceAndPath("player", "ping_colored"), (ctx, arg) -> {
            if (ctx.hasServerPlayer()) {
                int x = ctx.serverPlayer().connection.latency();
                return PlaceholderResult.value(Component.literal(String.valueOf(x)).withStyle(x < 100 ? ChatFormatting.GREEN : x < 200 ? ChatFormatting.GOLD : ChatFormatting.RED));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "displayname"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getDisplayName());
            } else if (ctx.hasNameAndId()) {
                return PlaceholderResult.value(Component.nullToEmpty(ctx.nameAndId().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Placeholders.getCommonPlaceholders().get(Identifier.fromNamespaceAndPath("player", "displayname"))
                .withId(Identifier.fromNamespaceAndPath("player", "display_name")));

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "displayname_visual"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(GeneralUtils.removeHoverAndClick(ctx.entity().getDisplayName()));
            } else if (ctx.hasNameAndId()) {
                return PlaceholderResult.value(Component.nullToEmpty(ctx.nameAndId().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Placeholders.getCommonPlaceholders().get(Identifier.fromNamespaceAndPath("player", "displayname_visual"))
                .withId(Identifier.fromNamespaceAndPath("player", "display_name_visual")));

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "displayname_unformatted"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(Component.literal(ctx.entity().getDisplayName().getString()));
            } else if (ctx.hasNameAndId()) {
                return PlaceholderResult.value(Component.nullToEmpty(ctx.nameAndId().name()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });
        Placeholders.registerCommon(Placeholders.getCommonPlaceholders().get(Identifier.fromNamespaceAndPath("player", "displayname_unformatted")).withId(Identifier.fromNamespaceAndPath("player", "display_name_unformatted")));

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "inventory_slot"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    int slot = Integer.parseInt(arg);

                    var inventory = ctx.player().getInventory();

                    if (slot >= 0 && slot < inventory.getContainerSize()) {
                        var stack = inventory.getItem(slot);

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

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "inventory_slot_no_rarity"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    int slot = Integer.parseInt(arg);

                    var inventory = ctx.player().getInventory();

                    if (slot >= 0 && slot < inventory.getContainerSize()) {
                        var stack = inventory.getItem(slot);

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

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "equipment_slot"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    var slot = EquipmentSlot.byName(arg);

                    var stack = ctx.player().getItemBySlot(slot);
                    return PlaceholderResult.value(GeneralUtils.getItemText(stack, true));
                } catch (Exception e) {
                    // noop
                }
                return PlaceholderResult.invalid("Invalid argument");
            } else {
                return PlaceholderResult.invalid("No player or invalid argument!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "equipment_slot_no_rarity"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    var slot = EquipmentSlot.byName(arg);

                    var stack = ctx.player().getItemBySlot(slot);
                    return PlaceholderResult.value(GeneralUtils.getItemText(stack, false));
                } catch (Exception e) {
                    // noop
                }
                return PlaceholderResult.invalid("Invalid argument");
            } else {
                return PlaceholderResult.invalid("No player or invalid argument!");
            }
        });

        Placeholders.registerServer(Identifier.fromNamespaceAndPath("player", "playtime"), (ctx, arg) -> {
            if (ctx.hasServerPlayer()) {
                int x = ctx.serverPlayer().getStats().getValue(Stats.CUSTOM.get(Stats.PLAY_TIME));
                return PlaceholderResult.value(arg != null
                        ? DurationFormatUtils.formatDuration((long) x * 50, arg, true)
                        : GeneralUtils.durationToString((long) x / 20)
                );
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerServer(Identifier.fromNamespaceAndPath("player", "statistic"), (ctx, arg) -> {
            if (ctx.hasServerPlayer() && arg != null) {
                try {
                    var args = arg.split(" ");

                    if (args.length == 1) {
                        var identifier = Identifier.tryParse(args[0]);
                        if (identifier != null) {
                            var stat = Stats.CUSTOM.get(BuiltInRegistries.CUSTOM_STAT.getValue(identifier));
                            int x = ctx.serverPlayer().getStats().getValue(stat);
                            return PlaceholderResult.value(stat.format(x));
                        }
                    } else if (args.length >= 2) {
                        var type = Identifier.tryParse(args[0]);
                        var id = Identifier.tryParse(args[1]);
                        if (type != null) {
                            var statType = (StatType<Object>) BuiltInRegistries.STAT_TYPE.getValue(type);

                            if (statType != null) {
                                var key = statType.getRegistry().getValue(id);
                                if (key != null) {
                                    var stat = statType.get(key);
                                    int x = ctx.serverPlayer().getStats().getValue(stat);
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

        Placeholders.registerServer(Identifier.fromNamespaceAndPath("player", "statistic_raw"), (ctx, arg) -> {
            if (ctx.hasServerPlayer() && arg != null) {
                try {
                    var args = arg.split(" ");

                    if (args.length == 1) {
                        var identifier = Identifier.tryParse(args[0]);
                        if (identifier != null) {
                            var stat = Stats.CUSTOM.get(BuiltInRegistries.CUSTOM_STAT.getValue(identifier));
                            int x = ctx.serverPlayer().getStats().getValue(stat);
                            return PlaceholderResult.value(String.valueOf(x));
                        }
                    } else if (args.length >= 2) {
                        var type = Identifier.tryParse(args[0]);
                        var id = Identifier.tryParse(args[1]);
                        if (type != null) {
                            var statType = (StatType<Object>) BuiltInRegistries.STAT_TYPE.getValue(type);

                            if (statType != null) {
                                var key = statType.getRegistry().getValue(id);
                                if (key != null) {
                                    var stat = statType.get(key);
                                    int x = ctx.serverPlayer().getStats().getValue(stat);
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

        Placeholders.registerServer(Identifier.fromNamespaceAndPath("player", "objective"), (ctx, arg) -> {
            if (ctx.hasPlayer() && arg != null) {
                try {
                    ServerScoreboard scoreboard = ctx.server().getScoreboard();
                    Objective scoreboardObjective = scoreboard.getObjective(arg);
                    if (scoreboardObjective == null) {
                        return PlaceholderResult.invalid("Invalid objective!");
                    }
                    ReadOnlyScoreInfo score = scoreboard.getPlayerScoreInfo(ctx.player(), scoreboardObjective);
                    return PlaceholderResult.value(String.valueOf(score.value()));
                } catch (Exception e) {
                    /* Into the void you go! */
                }
                return PlaceholderResult.invalid("Invalid objective!");
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "facing"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getNearestViewDirection().getSerializedName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "facing_axis"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                var facing = ctx.entity().getNearestViewDirection();
                return PlaceholderResult.value(
                        (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? "-" : "+") + facing.getAxis().getSerializedName().toUpperCase(Locale.ROOT));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "horizontal_facing"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                return PlaceholderResult.value(ctx.entity().getDirection().getSerializedName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "horizontal_facing_axis"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                var facing = ctx.entity().getDirection();
                return PlaceholderResult.value(
                        (facing.getAxisDirection() == Direction.AxisDirection.NEGATIVE ? "-" : "+") + facing.getAxis().getSerializedName().toUpperCase(Locale.ROOT));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "pos_x"), (ctx, arg) -> {
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

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "pos_y"), (ctx, arg) -> {
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

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "pos_z"), (ctx, arg) -> {
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

        Placeholders.registerServer(Identifier.fromNamespaceAndPath("player", "pos_x_scaled"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                ServerLevel otherWorld = null;

                if (arg != null) {
                    var worldId = Identifier.tryParse(arg);
                    if (worldId != null) {
                        otherWorld = ctx.server().getLevel(ResourceKey.create(Registries.DIMENSION, worldId));
                    }
                }

                if (otherWorld == null) {
                    otherWorld = ctx.server().overworld();
                }

                double value = ctx.entity().getX() * DimensionType.getTeleportationScale(ctx.entity().level().dimensionType(), otherWorld.dimensionType());
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

        Placeholders.registerServer(Identifier.fromNamespaceAndPath("player", "pos_y_scaled"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                ServerLevel otherWorld = null;

                if (arg != null) {
                    var worldId = Identifier.tryParse(arg);
                    if (worldId != null) {
                        otherWorld = ctx.server().getLevel(ResourceKey.create(Registries.DIMENSION, worldId));
                    }
                }

                if (otherWorld == null) {
                    otherWorld = ctx.server().overworld();
                }

                double value = ctx.entity().getY() * DimensionType.getTeleportationScale(ctx.entity().level().dimensionType(), otherWorld.dimensionType());
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

        Placeholders.registerServer(Identifier.fromNamespaceAndPath("player", "pos_z_scaled"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                ServerLevel otherWorld = null;

                if (arg != null) {
                    var worldId = Identifier.tryParse(arg);
                    if (worldId != null) {
                        otherWorld = ctx.server().getLevel(ResourceKey.create(Registries.DIMENSION, worldId));
                    }
                }

                if (otherWorld == null) {
                    otherWorld = ctx.server().overworld();
                }

                double value = ctx.entity().getZ() * DimensionType.getTeleportationScale(ctx.entity().level().dimensionType(), otherWorld.dimensionType());
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

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "uuid"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(ctx.player().getStringUUID());
            } else if (ctx.hasNameAndId()) {
                return PlaceholderResult.value(Component.nullToEmpty("" + ctx.nameAndId().id()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "health"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "max_health"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getMaxHealth()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "hunger"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.valueOf(ctx.player().getFoodData().getFoodLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "saturation"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                return PlaceholderResult.value(String.format("%.0f", ctx.player().getFoodData().getSaturationLevel()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "locator_color"), (ctx, arg) -> {
            if (ctx.hasEntity() && ctx.entity() instanceof WaypointTransmitter waypoint) {
                var color = waypoint.waypointIcon().color.orElseGet(() -> ARGB.scaleRGB(ARGB.color(255, ctx.entity().getUUID().hashCode()), 0.9F)) & 0xFFFFFF;
                return PlaceholderResult.value(String.format(Locale.ROOT, "#%06X", color));
            } else {
                return arg != null ? PlaceholderResult.value(Component.literal(arg)) : PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "team_color"), (ctx, arg) -> {
            if (ctx.hasEntity()) {
                var team = ctx.entity().getTeam();
                return PlaceholderResult.value(team == null ? (arg != null ? Component.literal(arg) : Component.literal("white")) : Component.literal(team.getColor().getSerializedName()));
            } else {
                return arg != null ? PlaceholderResult.value(Component.literal(arg)) : PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "team_name"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                var team = ctx.player().getTeam();
                return PlaceholderResult.value(team == null ? Component.empty() : Component.nullToEmpty(team.getName()));
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "team_displayname"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                var team = ctx.player().getTeam();
                return PlaceholderResult.value(team == null ? Component.empty() : team.getDisplayName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "team_displayname_formatted"), (ctx, arg) -> {
            if (ctx.hasPlayer()) {
                var team = ctx.player().getTeam();
                return PlaceholderResult.value(team == null ? Component.empty() : team.getFormattedDisplayName());
            } else {
                return PlaceholderResult.invalid("No player!");
            }
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "biome"), (ctx, arg) -> {
            if (!ctx.hasLevel() && !ctx.hasBlockPosition()) {
                return PlaceholderResult.invalid("Missing level and/or block position!");
            }

            var world = ctx.level();
            var pos = ctx.blockPosition();

            var biome = world.getBiome(pos);
            if (biome.unwrapKey().isEmpty()) {
                return PlaceholderResult.invalid("No biome key??");
            }

            return PlaceholderResult.value(Component.translatable(biome.unwrapKey().get().identifier().toLanguageKey("biome"), biome.unwrapKey().get().identifier().toString()));
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "biome_raw"), (ctx, arg) -> {
            if (!ctx.hasLevel() && !ctx.hasBlockPosition()) {
                return PlaceholderResult.invalid("Missing level and/or block position!");
            }

            var world = ctx.level();
            var pos = ctx.blockPosition();

            var biome = world.getBiome(pos);
            if (biome.unwrapKey().isEmpty()) {
                return PlaceholderResult.invalid("No biome key??");
            }

            return PlaceholderResult.value(biome.unwrapKey().get().identifier().toString());
        });

        Placeholders.registerCommon(Identifier.fromNamespaceAndPath("player", "head"), (ctx, arg) -> {
            if (!ctx.hasGameProfile()) {
                return PlaceholderResult.invalid("No Game Profile!");
            }

            return PlaceholderResult.value(Component.object(new PlayerSprite(ResolvableProfile.createResolved(ctx.gameProfile()), SimpleArguments.bool(arg, true))));
        });
    }
}
