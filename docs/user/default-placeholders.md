# Default placeholder list

These placeholders are provided by default and are available for every mod using Placeholder API.
If placeholder isn't parsed, make sure it is used in correct context, with valid arguments and
that you are using the latest version.

Prior to 1.19, arguments were separated with a slash (`/`) instead of space.

## List of placeholders

### Player

!!! tip inline end "Vanilla Statistics"

    A list of `[statistic]`s can be found on the [Minecraft Wiki](https://minecraft.wiki/w/Statistics#List_of_custom_statistic_names)

    A list of `[type]`s can be found on the [Minecraft Wiki](https://minecraft.wiki/w/Statistics#Statistic_types_and_names)

    Examples: `%player:statistic play_time%`, `%player:statistic mined diamond_ore%`

- `%player:name%` - The player's name.
- `%player:name_visual%` - The player's name (without hover and click action).
- `%player:name_unformatted%` - The player's name (without formatting).
- `%player:displayname%` - The player's display name (used on chat).
- `%player:displayname_visual%` - The player's display name (without hover and click action).
- `%player:displayname_unformatted%` - The player's display name (without formatting).
- `%player:ping%` - The player's ping.
- `%player:ping_colored%` - The player's ping (colored).
- `%player:pos_x%` - The player's `x` coordinate.
- `%player:pos_y%` - The player's `y` coordinate.
- `%player:pos_z%` - The player's `z` coordinate.
- `%player:health%` - The player's health.
- `%player:max_health%` - The player's max health.
- `%player:hunger%` - The player's hunger.
- `%player:saturation%` - The player's saturation.
- `%player:inventory_slot [slot number]%` - The item in player's inventory at slot `number`.
- `%player:equipment_slot [name]%` - The player's equipment at selected slot. Valid values for `[name]`
  are `mainhand`, `offhand`, `head`, `chest`, `legs` and `feet`.
- `%player:playtime%`/`%player:playtime [formatting]%` - The player's playtime.
- `%player:statistic [statistic]%`/`%player:statistic [type] [statistic]%` - The formatted value of player's statistic.
- `%player:statistic_raw [statistic]%`/`%player:statistic_raw [type] [statistic]%` - The value of player's statistic.
- `%player:objective [objective]%` - The value of player's scoreboard objective.

### World

!!! info inline end "Valid Groups"

    Valid values for `[group]` are `monster`, `creature`, `ambient`, `axolotls`, `underground_water_creature`, `water_creature`,
    `water_ambient`, and `misc`.

- `%world:time%` - The world's time (Format: `HH:MM`).
- `%world:time_alt%` - The world's time (alternative formatting) (Format: `HH:MM AM/PM`).
- `%world:day%` - The world's day.
- `%world:player_count%` - The world's player count.
- `%world:mob_count%`/`%world:mob_count [group]%` - Shows amount of spawned mobs.
- `%world:mob_cap%`/`%world:mob_cap [group]%` - Shows maximum amount of mobs that can spawn is player's world.
- `%world:id%` - The world's ID.
- `%world:name%` - The world's name.

### Server

- `%server:tps%` - The server's TPS.
- `%server:tps_colored%` - The server's TPS (colored).
- `%server:mspt%` - The server's MSPT.
- `%server:mspt_colored%` - The server's MSPT (colored).
- `%server:time%`/`%server:time [formatting]%` - The server's time.
- `%server:uptime%`/`%server:uptime [formatting]%` (2.1.2+)- The server's uptime.
- `%server:version%` - The server's version.
- `%server:name%` - The server's name.
- `%server:motd%` - The server's motd.
- `%server:used_ram%`/`%server:used_ram [gb]%` - The amount of ram used by server. (If the argument `gb` is added, it is displayed in
  gigabytes)
- `%server:max_ram%`/`%server:max_ram [gb]%` - The maximum amount of ram that can be used by server. (If the argument `gb` is added, it is
  displayed in gigabytes)
- `%server:online%` - The number of online players.
- `%server:max_players%` - The maximum player count.
- `%server:brand%` (2.1.3+) - Returns server's brand ("fabric"/"quilt"/etc).
- `%server:mod_count%` (2.1.3+) - Returns amount of installed mods.
- `%server:mod_version [modid]%` - Returns version of the specified mod.
- `%server:mod_name [modid]%` - Returns name of the specified mod.
- `%server:mod_description [modid]%` - Returns description of the specified mod.
- `%server:objective_name_top [objective] [position]%` - Shows name of the player at the `position`th place in the scoreboard `objective`.
- `%server:objective_score_top [objective] [position]%` - Shows score of the player at the `position`th place in the scoreboard `objective`.

*[TPS]: Ticks Per Second. The number of ticks per second executing on the server. <20 TPS means the server is lagging.
*[MSPT]: Milliseconds Per Tick. The number of milliseconds it takes for a tick on the server. >50 MSPT means the server is lagging.
