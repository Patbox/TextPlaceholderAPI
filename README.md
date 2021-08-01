# About Placeholder API
It's a small, jij-able API that allows creation and parsing placeholders within strings and Minecraft Text Components.
Placeholders use simple format of `%modid:type%` or `%modid:type/data%`.
It also includes simple, general usage text format indented for simplifying user input in configs/chats/etc.

For list of currently available placeholders, check [wiki](https://github.com/Patbox/TextPlaceholderAPI/wiki).

## Usage:
Add it to your dependencies like this:

```
repositories {
	maven { url 'https://maven.nucleoid.xyz' }
}

dependencies {
	modImplementation include("eu.pb4.placeholder-api:[TAG]").
}
```
## Creating placeholders:
To add own placeholders, you just need to use `PlaceholderAPI.register(Identifier, PlaceholderHandler)`, like in example below.
```
PlaceholderAPI.register(new Identifier("server", "name"), (ctx) -> PlaceholderResult.value(ctx.getServer().getName()));
```

## Parsing placeholder:
If you want to parse placeholders, you need to use `PlaceholderAPI.parseString(String, MinecraftServer or ServerPlayerEntity)`
for strings or `PlaceholderAPI.parseText(Text, MinecraftServer or ServerPlayerEntity)` for Vanilla Text Components.
These function don't modify original one, instead they just return new String/Text.


