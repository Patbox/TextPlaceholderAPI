# Simplified Text Format

It's a simple, string format inspired by formats like HTML or MiniMessage.
It was created to allow quick and readable way of formatting Minecraft Text Components
while still providing all of its functionality as opposed for legacy &/§ formatting
used by bukkit and bukkit-based plugins.

## Structure

Formatting is build on concept of tags. 

Most of them come in pairs of a starting (`<tag>`) and closing one (`</tag>`).
While closing ones are technically optional, without them formatting will continue until end of
an input text or special `<reset>` tag. Some tags support arguments, which can be passed by adding `:`
after tag name in starting one (for example `<color:#FF3333> </color>`). Arguments containing symbols like 
`:`, `<`, `>`, `%` and spaces should be wrapped in a `'` symbols (for example `<hover:show_text:'<red>Hello!'>...`).
In case you want to type `<tag>` as plain text, you need to prefix it with `\ ` symbol .

Few examples:

* `<color:#11dddd>%player:displayname%</color> <dark_gray>»</dark_gray> <color:#cccccc>${message}`
* `<red>Hello <blue>world</blue>!</red>`
* `<rainbow>Some colors for you`

There are also few self-contained tags, that don't require closing ones. They can also take arguments 
in the same way to previous ones.

Few examples:

* `<lang:'item.minecraft.diamond'>`
* `<reset>`


## List of available tags
Here is list of all default tags available. Other mods can add new or limit usage
of existing ones, so not every might work in yours case.

### Colors
By default, there are multiple tags representing colors. They use their vanilla name or (additional aliases).
This tag should be closed.

The current list includes: `<yellow>`, `<dark_blue>`, `<dark_purple>`, `<gold>`, `<red>`, `<aqua>`, 
`<gray>`, `<light_purple>`, `<white>`, `<dark_gray>`, `<green>`, `<dark_green>`, `<blue>`, `<dark_aqua>`, 
`<dark_green>`, `<black>`

There is also a universal `<color:[value]>` and `<c:[value]>` tags, in which you can replace `[value]` with vanilla color name or
a rgb color starting with `#` (for example `<color:#AABBCC>`)

### Decorations
These tags allow decorating text, they are quite simple.

This tag should be closed.

* `<strikethrough>`/`<st>` - Makes text strikethrough,
* `<underline>` - Underlines text,
* `<italic>`/`<i>` - Makes text italic,
* `<obfuscated>`/`<obf>` - Obfuscates text (matrix effect),
* `<bold>`/`<b>` - Makes text bold,

### Click events
Click events allow making text more interactive. They should be however limited to admin usage only, 
as they can do harm if accessible by normal players.

This tag should be closed.

There are few available actions:

* `<click:open_url:[value]>`/`<open_url:[value]>`/`<url:[value]>` - Opens provided url
* `<click:run_command:[value]>`/`<run_cmd:[value]>` - Runs command as player
* `<click:suggest_command:[value]>`/`<suggest_command:[value]>`/`<cmd:[value]>` - Suggests command to player
* `<click:copy_to_clipboard:[value]>`/`<copy_to_clipboard:[value]>`/`<copy:[value]>` - Copies text to clipboard
* `<click:change_page:[value]>`/`<change_page:[value]>`/`<page:[value]>` - Changes page in a book

`[value]` needs to be replaced with targeted value, for example `'gamemode creative'`

### Hover
Hover tag allows adding simple hover on text. It can be used to display additional information.
This tag should be closed.

* `<hover:show_text:[value]>`/`<hover:[value]>` - Adds simple text hover (`[value]` uses the same formatting as rest)
* `<hover:show_item:[value]>` - Adds simple ItemStack hover (`[value]` is item in sNBT format)
* `<hover:show_entity:[type]:[UUID]:'Display Name'>` - Adds entity hover (`:` in entity type needs to be replaced with `\:`)

### Fonts
This tag allows you to change font to any build in one or one provided by resource pack.

You can use it by simply adding `<font:[value]>`, where `[value]` is just a font name.
Minecraft has 3 build-in fonts: `default`, `uniform` and `alt`.

This tag should be closed.


### Inserting
This tag creates a clickable text, that inserts its value at the end of player's chat message.

You can use it by writing `<insert:[value]>`, where `[value]` inserted text (should be wrapped in `'`).

This tag should be closed.


### Translations
Translations tag allows you to insert a text from a lang file (including ones parsed on servers by some mods).

You use it with `<lang:[key]:[optional arg 1]:[optional arg 1]:...>`, where `[key]` is a translation key 
and `[optional arg X]` are optional, fully formatted arguments you can pass.

This tag is self containing, so it doesn't contain a closing tag.

### Control keys
This tag allows you to add information about player control keys, with respecting of theirs configuration.

You can use it with `<key:[value]>`, where `[value]` is a control key used, which you can hind [here](https://minecraft.fandom.com/wiki/Controls#Configurable_controls).

This tag is self containing, so it doesn't contain a closing tag.

### Gradients
This tag allows you to add gradients to the text. However, it has multiple limitation that can 
block its usage. Currently, you can't use dynamic values (translations, control keys, placeholders, etc)
within them, as they require static text.

There 2 types of gradients:

* `<gradient:[color 1]:[color 2]:...>`/`<gr:[color 1]:[color 2]:...>` - I can take multiple colors to move between them smoothly. 
* `<rainbow:[frequency]:[saturation]:[offset]>`/`<rb:[...]>` - It's simple rainbow gradient. All arguments are optional (`<ranbow>` is still valid) and they take numbers between 0 and 1 (`0.3` for example)


### Reset
`<reset>` and `<r>` are special, self-contained tags which close all previous formatting. They are
useful in cases, where you want to close multiple formatting tags quickly
  

