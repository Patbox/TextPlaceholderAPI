# Simplified Text Format

It's a simple, string format inspired by formats like HTML or MiniMessage.
It was created to allow quick and readable way of formatting Minecraft Text Components
while still providing all of its functionality as opposed for legacy &/§ formatting
used by bukkit and bukkit-based plugins.

## Structure

Formatting is build on concept of tags.

??? warning inline end "Nesting Quotations"

    In the rare circumstance  you need to nest quations, they must be escaped: `\'`.
    For example,
    ```
    <lang:'chat.type.team.sent':'<hover\:\'<lang\:chat.type.team.hover>\'><suggest_command\:\'/teammsg \'>${team}':'${displayName}':'${message}'>
    ```

Most of them come in pairs of a starting (`#!xml <tag>`) and closing one (`#!xml </tag>` for direct or `#!xml <‍/‍>` (2.1.3+) for automatic).
While closing ones are technically optional, without them formatting will continue until end of
an input text or special `#!xml <reset>` tag. Some tags support arguments, which can be passed by adding `:`
after tag name in starting one (for example `#!xml <color:#FF3333> </color>`). Arguments containing symbols like
`:`, `<`, `>`, `%` and spaces should be wrapped in a `'` symbols (for example `#!xml <hover:show_text:'<red>Hello!'>...`).

In case you want to type `#!xml <tag>` as plain text, you need to prefix it with `\ ` symbol .

???+ example

    - `#!xml <color:#11dddd>%player:displayname%</color> <dark_gray>»</dark_gray> <color:#cccccc>${message}`
    - `#!xml <red>Hello <blue>world</blue>!</red>`
    - `#!xml <rainbow>Some colors for you`

There are also few self-contained tags, that don't require closing ones. They can also take arguments
in the same way to previous ones.

Few examples:

- `#!xml <lang:'item.minecraft.diamond'>`
- `#!xml <reset>`

## List of available tags

Here is list of all default tags available. Other mods can add new or limit usage
of existing ones, so not every might work in yours case.

### Colors

!!! note inline end

      This tag should be closed.

By default, there are multiple tags representing colors. They use their vanilla name or (additional aliases).

The current list includes: `#!xml <yellow>`, `#!xml <dark_blue>`, `#!xml <dark_purple>`, `#!xml <gold>`, `#!xml <red>`, `#!xml <aqua>`,
`#!xml <gray>`, `#!xml <light_purple>`, `#!xml <white>`, `#!xml <dark_gray>`, `#!xml <green>`, `#!xml <dark_green>`, `#!xml <blue>`,
`#!xml <dark_aqua>`, `#!xml <dark_green>`, `#!xml <black>`

There is also a universal `#!xml <color:[value]>` and `#!xml <c:[value]>` tags, in which you can replace `[value]` with vanilla color name
or an rgb color starting with `#` (for example `#!xml <color:#AABBCC>`)

### Decorations

!!! note inline end

      This tag should be closed.

These tags allow decorating text, they are quite simple.

- `#!xml <strikethrough>`/`#!xml <st>` - Makes the text strikethrough,
- `#!xml <underline>`/`#!xml <underlined>` - Underlines the text,
- `#!xml <italic>`/`#!xml <i>` - Makes the text italic,
- `#!xml <obfuscated>`/`#!xml <obf>` - Obfuscates the text (matrix effect),
- `#!xml <bold>`/`#!xml <b>` - Makes the text bold,

### Click events

!!! note inline end

      This tag should be closed.

!!! danger

    They should be however limited to admin usage only, as they can do harm if accessible by normal players.

Click events allows making text more interactive.

There are few available actions:

- `#!xml <click:open_url:[value]>`/`#!xml <open_url:[value]>`/`#!xml <url:[value]>` - Opens the provided url
- `#!xml <click:run_command:[value]>`/`#!xml <run_cmd:[value]>` - Runs a command as the player
- `#!xml <click:suggest_command:[value]>`/`#!xml <suggest_command:[value]>`/`#!xml <cmd:[value]>` - Suggests a command to the player
- `#!xml <click:copy_to_clipboard:[value]>`/`#!xml <copy_to_clipboard:[value]>`/`#!xml <copy:[value]>` - Copies text to the clipboard
- `#!xml <click:change_page:[value]>`/`#!xml <change_page:[value]>`/`#!xml <page:[value]>` - Changes the page in a book

`[value]` needs to be replaced with targeted value, for example `'gamemode creative'`

### Hover

Hover tag allows adding simple hover on text. It can be used to display additional information.

!!! note inline end

      This tag should be closed.

- `#!xml <hover:show_text:[value]>`/`#!xml <hover:[value]>` - Adds a simple text hover (`[value]` uses the same formatting as rest)
- `#!xml <hover:show_item:[value]>` - Adds a simple ItemStack hover (`[value]` is item in sNBT format)
- `#!xml <hover:show_entity:[type]:[UUID]:'Display Name'>` - Adds an entity hover.
  (The colon (`:`) in the entity type needs to be replaced with `\:`.
  For example, `#!xml <hover:show_entity:minecraft\:bee:[UUID]:'Display Name'>`)

### Fonts

!!! note inline end

      This tag should be closed.

This tag allows you to change font to any build in one or one provided by resource pack.

You can use it by simply adding `#!xml <font:[value]>`, where `[value]` is just a font name.
Minecraft has 3 build-in fonts: `default`, `uniform` and `alt`.

### Inserting

!!! note inline end

      This tag should be closed.

This tag creates a clickable text, that inserts its value at the end of player's chat message.

You can use it by writing `#!xml <insert:[value]>`, where `[value]` inserted text (should be wrapped in `'`).

### Translations

!!! note inline end

    This tag is self containing, so it doesn't contain a closing tag.

Translations tag allows you to insert a text from a lang file (including ones parsed on servers by some mods).

You use it with `#!xml <lang:[key]:[optional arg 1]:[optional arg 1]:...>`, where `[key]` is a translation key
and `[optional arg X]` are optional, fully formatted arguments you can pass.

### Control keys

!!! question inline end "Control Keys"

    You can find a list of control keys on the [Minecraft Wiki](https://minecraft.wiki/w/Controls#Configurable_controls)

!!! note inline end

    This tag is self containing, so it doesn't contain a closing tag.

This tag allows you to add information about player control keys, with respecting of theirs configuration.

You can use it with `#!xml <keybind:[value]>`, where `[value]` is a control key used.

### Gradients

This tag allows you to add gradients to the text. However, it has multiple limitation that can
block its usage. Currently, you can't use dynamic values (translations, control keys, placeholders, etc)
within them, as they require static text.

There 2 types of gradients:

- `#!xml <gradient:[color 1]:[color 2]:...>`/`#!xml <gr:[color 1]:[color 2]:...>` - I can take multiple colors to move between them
  smoothly.
- `#!xml <hard_gradient:[color 1]:[color 2]:...>`/`#!xml <hgr:[color 1]:[color 2]:...>` - I can take multiple colors to move between them
  without mixing them.
- `#!xml <rainbow:[frequency]:[saturation]:[offset]>`/`#!xml <rb:[...]>` - It's simple rainbow gradient. All arguments are
  optional (`#!xml <ranbow>` is still valid) and they take numbers between 0 and 1 (`0.3` for example)

### Reset

`#!xml <reset>` and `#!xml <r>` are special, self-contained tags which close all previous formatting. They are
useful in cases, where you want to close multiple formatting tags quickly

### Clear (2.1.3+)

!!! note inline end

      This tag should be closed.

This tag allows you to clear any formatting within this tag, without leaving any visible tags. It also
works with placeholders, which gives a bit more flexibility.

This tag can work without arguments making it clear all formatting or with them limiting clearing to selected types.

Examples:

- `#!xml <clear>` - Removes all formatting, leaving only text.
- `#!xml <clear:hover>` - Removes all hovers.
- `#!xml <clear:hover:color>` - Removes all hovers and colors.

Supported arguments: `color`, `bold`, `italic`, `strikethrough`, `underline`, `hover`, `click`, 
`insertion`, `font`, `all`.