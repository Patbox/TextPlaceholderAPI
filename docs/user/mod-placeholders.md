# Mod placeholders list

These placeholders are provided by other mods. Some are build in directly, while others require an addon.

## List of placeholders

### [AfkPlus](https://modrinth.com/mod/afkplus)

- `%afkplus:afk%` - Returns a configurable "[AFK]" tag if the player is marked as AFK.
- `%afkplus:name`/`%afkplus:display_name%` - Returns a configurable replacement for `%player:displayname%` if the player is marked as AFK.
  This allows for backport formatting, and it can fully support LuckPerms Prefixes, and other mods.  By default it returns the standard
  `%player:displayname%` when not AFK, which is also configurable.
- `%afkplus:duration%` - Returns the (HH-mm-SS.ss) duration since a player went AFK, or nothing.
  Configurable in a more human readable format, ie. (5 minutes, 3 seconds).
- `%afkplus:time%` - Returns the time (yyyy-MM-dd_HH.mm.ss) since a player went AFK, or nothing.
- `%afkplus:reason%` - Returns the reason why a player went AFK, or nothing.

### [Get Off My Lawn ReServed](https://pb4.eu/#get-off-my-lawn)

- `%goml:claim_owners%`/`%goml:claim_owners [no owners text]%` - Returns a list of claim owners.
- `%goml:claim_owners_uuid%`/`%goml:claim_owners_uuid [no owners text]%` - Returns a list of claim owners (as UUIDs).
- `%goml:claim_trusted%`/`%goml:claim_trusted [no trusted text]%` - Returns a list of trusted players.
- `%goml:claim_trusted_uuid%`/`%goml:claim_trusted_uuid [no trusted text]%` - Returns a list of trusted players (as UUIDs).
- `%goml:claim_info%`/`%goml:claim_info [no claim text]:[can build text]:[can't build text]%` - Returns the info for a claim.
  (variables: `${owners}`, `${owners_uuid}`, `${trusted}`, `${trusted_uuid}`, `${anchor}`)

### [Luckperms](https://luckperms.net/)

!!! warning inline end "Dependency"

    Requires [LuckPerms Fabric PlaceholderAPI addon](https://ci.lucko.me/job/LuckPermsPlaceholders/)

- `%luckperms:prefix%` - Returns the player's prefix.
- `%luckperms:suffix%` - Returns the players suffix.
- `%luckperms:meta [meta key]%` - Returns a single value for the given meta key.
- `%luckperms:meta_all [meta key]%` - Returns all assigned values for the given meta key.
- `%luckperms:prefix_element [element]%` - Returns a prefix element using the given "meta stack" element definition.
  See [Prefix Stacking](https://luckperms.net/wiki/Prefix-&-Suffix-Stacking)
- `%luckperms:suffix_element [element]%` - Returns a suffix element using the given "meta stack" element definition.
  See [Prefix Stacking](https://luckperms.net/wiki/Prefix-&-Suffix-Stacking).
- `%luckperms:context%`/`%luckperms:context [context key]%` - Returns all of the players current contexts. If a key is
  given as an argument, then only the values corresponding to the given key are returned.
- `%luckperms:groups%` - Returns a list of the groups directly inherited by the player.
- `%luckperms:inherited_groups%` - Returns a list of all of the groups inherited (directly or indirectly) by the player.
- `%luckperms:primary_group_name%` - Returns the name of the player's primary group.
- `%luckperms:has_permission [permission]%` - Returns if the player directly has the exact given permission.
  (not the same as a permission check!)
- `%luckperms:inherits_permission [permission]%` - Returns if the player inherits the exact given permission.
  (not the same as a permission check!)
- `%luckperms:check_permission [permission]%` - Returns the result of a permission check for the given permission on the player.
- `%luckperms:in_group [group]%` - Returns if the player is directly a member of the given group.
- `%luckperms:inherits_group [group]%` - Returns if the player is a direct or indirect member of the given group.
- `%luckperms:on_track [track]%` - Returns if the player's "primary group" is on this track.
  (deprecated - avoid relying on primary groups, use the placeholder below instead!)
- `%luckperms:has_groups_on_track [track]%` - Returns if any of the groups the player is directly a member of is on the given track.
- `%luckperms:highest_group_by_weight%` - Returns the name of the players highest weighted group, not including groups they indirectly
  inherit from others.
- `%luckperms:lowest_group_by_weight%` - Returns the name of the players lowest weighted group, not including groups they indirectly inherit
  from others.
- `%luckperms:highest_inherited_group_by_weight%` - Returns the name of the players highest weighted group, including groups they indirectly
  inherit from others.
- `%luckperms:lowest_inherited_group_by_weight%` - Returns the name of the players lowest weighted group, including groups they indirectly
  inherit from others.
- `%luckperms:current_group_on_track [track]%` - If the player is currently on the given track, returns the name of the group.
- `%luckperms:next_group_on_track [track]%` - If the player is currently is currently on the given track, returns the name of the next
  group. (the one they would be promoted to next)
- `%luckperms:previous_group_on_track [track]%` - If the player is currently is currently on the given track, returns the name of the
  previous group. (the one they would be demoted to next)
- `%luckperms:first_group_on_tracks [tracks]%` - Given a comma separated list of track names, finds the first group inherited by the player
  on any of the given tracks.
- `%luckperms:last_group_on_tracks [tracks]%` - Given a comma separated list of track names, finds the last group inherited by the player on
  any of the given tracks.
- `%luckperms:expiry_time [permission]%` - Gets the duration remaining on a temporary permission assigned directly to the player.
- `%luckperms:inherited_expiry_time [permission]%` - Gets the duration remaining on a temporary permission assigned directly to or inherited
  by the player.
- `%luckperms:group_expiry_time [group name]%` - Gets the duration remaining on a temporary group membership assigned directly to the
  player.
- `%luckperms:inherited_group_expiry_time [group name]%` - Gets the duration remaining on a temporary group membership assigned directly to
  or inherited by the player.

### [PlayerEx](https://www.curseforge.com/minecraft/mc-mods/playerex)

- `%playerex:level%` - Shows current player level.
- `%playerex:name_top [position]%` - Shows name of the player at the `n`th place, where `n` is the `position` argument.
- `%playerex:level_top [position]%` - Shows level of the player at the `n`th place, where `n` is the `position` argument.

### [Player Pronouns](https://modrinth.com/mod/player-pronouns)

- `%playerpronouns:pronouns%` / `%playerpronouns:pronouns [default]%` - Displays formatted player's pronouns, or `default` if unset.
- `%playerpronouns:raw_pronouns%` / `%playerpronouns:raw_pronouns [default]%` - Displays player's pronouns as raw text, or `default` if
  unset. This allows text formatting to be embedded into the pronouns.

### [Spark](https://spark.lucko.me/download)

- `%spark:tps%` / `%spark:tps duration%` - Shows server TPS. `duration` can be `5s`, `10s`, `1m`, `5m`, or `15m`. Shows all durations if
  left out
- `%spark:tickduration%` / `%spark:tickduration [duration]%` - Shows the average tick durations. `duration` can be `10s` or `1m`. Shows all
  durations if left out
- `%spark:cpu_system%` / `%spark:cpu_system [duration]%` - Shows the average CPU usage for the whole system. `duration` can be `10s`, `1m`,
  or `15m`. Shows all durations if left out
- `%spark:cpu_process%` / `%spark:cpu_process [duration]%` - Shows the average CPU usage for the server process. `duration` can be `10s`,
  `1m`, or `15m`. Shows all durations if left out

### [Styled Nicknames](https://www.curseforge.com/minecraft/mc-mods/styled-nicknames)

- `%styled-nicknames:display_name%` - Either nickname or default player name. (skips other formatting mods)

### [Vanish](https://modrinth.com/mod/vanish)

- `%vanish:vanished%` - Displays a text (configurable via config) if a player is vanished.
- `%vanish:online%` - The amount of players that the player viewing the placeholder can see.

*[TPS]: Ticks Per Second. The number of ticks per second executing on the server. <20 TPS means the server is lagging.
*[MSPT]: Milliseconds Per Tick. The number of milliseconds it takes for a tick on the server. >50 MSPT means the server is lagging.
*[UUID]: UUIDs are used by minecraft to identify players and entities
*[UUIDs]: UUIDs are used by minecraft to identify players and entities
