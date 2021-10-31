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
Requires [Box of Placeholders addon](https://www.curseforge.com/minecraft/mc-mods/box-of-placeholders)

- `%luckperms:prefix%` / `%luckperms:prefix/[number]%` - One or more prefixes of player
- `%luckperms:suffix%` / `%luckperms:suffix/[number]%` - One or more suffixes of player
- `%luckperms:prefix_if_in_group/[group]%` - Returns player's prefix, if they are in selected group (otherwise empty)
- `%luckperms:suffix_if_in_group/[group]%` - Returns player's suffix, if they are in selected group (otherwise empty)
- `%luckperms:primary_group%` - Returns player's primary group
- `%luckperms:group_expiry_time/[group]%` - Time after which player's group is removed
- `%luckperms:permission_expiry_time/[permission]%` - Time after which player's permission is removed

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
