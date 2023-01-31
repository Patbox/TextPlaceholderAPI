# Using placeholders

Usage of placeholder mostly depends on implementation of mod itself. If mod uses simple, one/multiline text
(for example with Simple Text Format) you just need to add it by just writing `%placeholder%`
(or in some cases`{placeholder}`, `${placeholder}` or other format which should be provided on mods page).

Inner part of placeholder can take shape of either `category:placeholder` or `category:placeholder argument` (previously
`category:placeholder/argument` prior to 1.19), where `category` is replaced by type (`player`, `world`, etc) or ID of the mod and
`placeholder` is the placeholder itself.
Additionally, some placeholders might have additional or required argument provided after first space. It's format
fully depend on mod providing it.

You can check list of [build in placeholders here](/user/default-placeholders)
and [placeholders from mods here](/user/mod-placeholders).

### List of mods supporting displaying Placeholder API's placeholders:

!!! question inline end "Missing Your Mod?"

    Are you a mod dev, and your mod is missing?
    Feel free to open an issue!

- Styled Player List -
  [CurseForge](https://www.curseforge.com/minecraft/mc-mods/styled-player-list),
  [Modrinth](https://modrinth.com/mod/styledplayerlist),
  [Github](https://github.com/Patbox/StyledPlayerList)

- Styled Chat -
  [CurseForge](https://www.curseforge.com/minecraft/mc-mods/styled-chat),
  [Modrinth](https://modrinth.com/mod/styled-chat),
  [Github](https://github.com/Patbox/StyledChat)

- Holograms -
  [CurseForge](https://www.curseforge.com/minecraft/mc-mods/server-holograms),
  [Modrinth](https://modrinth.com/mod/holograms),
  [Github](https://github.com/Patbox/Holograms)

- Player Events -
  [CurseForge](https://www.curseforge.com/minecraft/mc-mods/player-events),
  [Github](https://github.com/ByMartrixx/player-events)

- Discord4Fabric -
  [CurseForge](https://www.curseforge.com/minecraft/mc-mods/discord4fabric),
  [Modrinth](https://modrinth.com/mod/discord4fabric),
  [Github](https://github.com/Reimnop/Discord4Fabric)

