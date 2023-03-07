# About Placeholder API

It's a small, JIJ-able API that allows creation and parsing placeholders within strings and Minecraft Text Components.
Placeholder API uses a simple format of `%modid:type%` or `%modid:type data%` (`%modid:type/data%` prior to 1.19).
It also includes simple, general usage text format indented for simplifying user input in configs/chats/etc.

## For users

It allows users to configure multiple mods in similar way without losing compatibility between mods.
Placeholders allow changing what and where any information is present within compatible mods.

Additionally, Simplified Text Format allows to style them in readable way without the requirement of writing JSON manually or using
generators.

- [Using placeholders](user/general)
- [Default placeholder list](user/default-placeholders)
- [Mod placeholder list](user/mod-placeholders)
- [Simplified Text Format](user/text-format)

## For developers

Usage of Placeholder API is a simple way to achieve good mod compatibility without having to implement
multiple mod specific apis. Additionally, the placeholder parsing system can be used for replacing
own static (or dynamic placeholders) in Text created by player or read from config. This with combination
of Simplified Text Format allows creating great user/admin experience.

- [Getting Started](dev/getting-started)
- [Adding placeholders](dev/adding-placeholders)
- [Parsing placeholders](dev/parsing-placeholders)
- [TextNodes and NodeParsers](dev/text-nodes)
- [Using Simplified Text Format (TextParserV1)](dev/text-format)

*[JIJ]: Jar-in-Jar
