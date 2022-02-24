# Mod placeholders list
These placeholders are provided by other mods. Some are build in directly, while others require an addon.

Box Of Placeholders is a mod that adds placeholder for other mods.
You can download it from https://github.com/Patbox/BoxOfPlaceholders/releases

## List of placeholders
### [Box of Placeholders](https://www.curseforge.com/minecraft/mc-mods/box-of-placeholders)
- `%bop:ram_max%` / `%bop:ram_max/gb%` - Shows maximal amount of ram
- `%bop:ram_used%` / `%bop:ram_used/gb%` - Shows amount of used ram
- `%bop:ram_free%` / `%bop:ram_free/gb%` - Shows amount of free ram
- `%bop:ram_used_percent%` - Shows amount of used ram (as percent)
- `%bop:ram_free_percent%` - Shows amount of free ram (as percent)
- `%bop:animation/[id]%` - Shows animation based on id
- `%bop:mob_count%`/`%bop:mob_count/[group]%` - Shows amount of spawned mobs
- `%bop:mob_cap%`/`%bop:mob_cap/[group]%` - Shows maximum amount of mobs that can spawn is player's world

### [Get Off My Lawn](https://www.curseforge.com/minecraft/mc-mods/get-off-my-lawn)
Requires [Box of Placeholders addon](https://www.curseforge.com/minecraft/mc-mods/box-of-placeholders)

- `%goml:claim_owners%`/`%goml:claim_owners/[No owners text]%` - Returns list of claim owners
- `%goml:claim_owners_uuid%`/`%goml:claim_owners_uuid/[No owners text]%` - Returns list of claim owners (as uuids)
- `%goml:claim_trusted%`/`%goml:claim_trusted/[No trusted text]%` - Returns list of trusted players
- `%goml:claim_trusted_uuid%`/`%goml:claim_trusted_uuid/[No trusted text]%` - Returns list of trusted players (as uuids)
- `%goml:claim_info%`/`%goml:claim_info/[no claim text]/[player can build]/[player can't build]%` - Returns list of trusted players (variables: `${owners}`, `${owners_uuid}`, `${trusted}`, `${trusted_uuid}`, `${anchor}`)

### [Luckperms](https://luckperms.net/)
Requires [LuckPerms Fabric PlaceholderAPI addon](https://ci.lucko.me/job/LuckPermsPlaceholders/)

- `%luckperms:prefix%` - Returns the player's prefix
- `%luckperms:suffix%` - Returns the players suffix
- `%luckperms:meta/[meta key]%` - Returns a single value for the given meta key
- `%luckperms:meta_all/[meta key]%` - Returns all assigned values for the given meta key
- `%luckperms:prefix_element/[element]%` - Returns a prefix element using the given "meta stack" element definition. See [Prefix Stacking](https://luckperms.net/wiki/Prefix-&-Suffix-Stacking)
- `%luckperms:suffix_element/[element]%` - Returns a suffix element using the given "meta stack" element definition. See [Prefix Stacking](https://luckperms.net/wiki/Prefix-&-Suffix-Stacking)
- `%luckperms:context/[context key]%` - Returns all of the players current contexts. If a key is given as an argument, then only the values corresponding to the given key are returned.
- `%luckperms:groups%` - Returns a list of the groups directly inherited by the player.
- `%luckperms:inherited_groups%` - Returns a list of all of the groups inherited (directly or indirectly) by the player.
- `%luckperms:primary_group_name%` - Returns the name of the player's primary group.
- `%luckperms:has_permission/[permission]%` - Returns if the player directly has the exact given permission (not the same as a permission check!)
- `%luckperms:inherits_permission/[permission]%%` - Returns if the player inherits the exact given permission (not the same as a permission check!)
- `%luckperms:check_permission/[permission]%%` - Returns the result of a permission check for the given permission on the player.
- `%luckperms:in_group/[group]%` - Returns if the player is directly a member of the given group.
- `%luckperms:inherits_group/[group]%` - Returns if the player is a direct or indirect member of the given group.
- `%luckperms:on_track/[track]%` - Returns if the player's "primary group" is on this track. (deprecated - avoid relying on primary groups, use the placeholder below instead!)
- `%luckperms:has_groups_on_track/[track]%` - Returns if any of the groups the player is directly a member of is on the given track.
- `%luckperms:highest_group_by_weight%` - Returns the name of the players highest weighted group, not including groups they indirectly inherit from others.
- `%luckperms:lowest_group_by_weight%` - Returns the name of the players lowest weighted group, not including groups they indirectly inherit from others.
- `%luckperms:highest_inherited_group_by_weight%` - Returns the name of the players highest weighted group, including groups they indirectly inherit from others.
- `%luckperms:lowest_inherited_group_by_weight%` - Returns the name of the players lowest weighted group, including groups they indirectly inherit from others.
- `%luckperms:current_group_on_track/[track]%` - If the player is currently on the given track, returns the name of the group.
- `%luckperms:next_group_on_track/[track]%` - If the player is currently is currently on the given track, returns the name of the next group (the one they would be promoted to next).
- `%luckperms:previous_group_on_track/[track]%` - If the player is currently is currently on the given track, returns the name of the previous group (the one they would be demoted to next).
- `%luckperms:first_group_on_tracks/[tracks]%` - Given a comma separated list of track names, finds the first group inherited by the player on any of the given tracks.
- `%luckperms:last_group_on_tracks/[tracks]%` - Given a comma separated list of track names, finds the last group inherited by the player on any of the given tracks.
- `%luckperms:expiry_time/[permission]%` - Gets the duration remaining on a temporary permission assigned directly to the player.
- `%luckperms:inherited_expiry_time/[permission]%` - %luckperms_inherited_expiry_time%
- `%luckperms:group_expiry_time/[group name]%` - Gets the duration remaining on a temporary group membership assigned directly to the player.
- `%luckperms:inherited_group_expiry_time/[group name]%` - Gets the duration remaining on a temporary group membership assigned directly to or inherited by the player.

### [PlayerEx](https://www.curseforge.com/minecraft/mc-mods/playerex)
- `%playerex:level%` - Shows current player level
- `%playerex:level_top_X%` - Shows player at X place (from 1 to 10, for example `%playerex:level_top_1%`)

### [Player Pronouns](https://modrinth.com/mod/player-pronouns)
- `%playerpronouns:pronouns%` / `%playerpronouns:pronouns/[default value]%` - Displays formatted player's pronouns
- `%playerpronouns:raw_pronouns%` / `%playerpronouns:raw_pronouns/[default value]%` - Displays formatted player's pronouns

### [Spark](https://spark.lucko.me/download)
- `%spark:tps%` / `%spark:tps/duration%` - Shows server TPS. `duration` can be `5s, 10s, 1m, 5m, 15m`. Shows all durations if left out
- `%spark:tickduration%` / `%spark:tickduration/duration%` - Shows the average tick durations. `duration` can be `10s, 1m`. Shows all durations if left out
- `%spark:cpu_system%` / `%spark:cpu_system/duration%` - Shows the average CPU usage for the whole system. `duration` can be `10s, 1m, 15m`. Shows all durations if left out
- `%spark:cpu_process%` / `%spark:cpu_process/duration%` - Shows the average CPU usage for the server process. `duration` can be `10s, 1m, 15m`. Shows all durations if left out

### [Styled Nicknames](https://www.curseforge.com/minecraft/mc-mods/styled-nicknames)
- `%styled-nicknames:display_name%` - Either nickname or default player name (skips other formatting mods)

### [Vanish](https://www.curseforge.com/minecraft/mc-mods/vanish)
Requires [Box of Placeholders addon](https://www.curseforge.com/minecraft/mc-mods/box-of-placeholders)

- `%vanish:safe_online%` - Returns (safe) number of player's online
- `%vanish:invisible_player_count%` - Number of player's using vanish
