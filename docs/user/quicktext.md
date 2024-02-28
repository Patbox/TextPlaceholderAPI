# QuickText Format (Beta)

!!! warn inline end

    Having issues with formatted arguments not working? It's possible the version of mod you use uses
    [Simplified Text Format](/user/text-format/) instead! Check it's documentation for more information!


It's a simple, but flexible, tag based text format designed for modern Minecraft.
It was created to allow quick and readable way of formatting Minecraft Text Components
while still providing all of its functionality as opposed for legacy &/§ formatting
used by bukkit and bukkit-based plugins.

## Structure

Formatting is build on concept of tags, comparable to html.

Most of them come in pairs of a starting (`#!xml <tag>`) and closing one (`#!xml </tag>` for closing last tag of selected type or `#!xml <‍/‍>` for last opened one.
While closing ones are technically optional, without them formatting will continue until end of
an input text or special `#!xml </*>` tag. Some tags support arguments, which are defined
after the name in ordered inline fashion (for example `#!xml <color #FF3333>`) or as key-value pair (for example `#!xml <hover value:'Hello!'>`).
You are allowed to mix inline and key-value defined arguments (in `KEY:VALUE` format). In that case, any key defined argument will be skipped from being read as ordered one.
Arguments spaces are required to be wrapped in a `'`, `"` or ``` symbols, with starting and ending symbols matching
(for example `#!xml <hover show_text '<red>Hello!'>...`, `#!xml <hover type:show_text value:'<red>Hello!'>...`).
If you want to use character used for wrapping, you can prefix it with backslash (for example as `\'`) or type it twice).

In case you want to type `#!xml <tag>` as plain text, you need to prefix it with `\ ` symbol .

???+ example

    - `#!xml <color #11dddd>%player displayname%</color> <dark_gray>»</dark_gray> <color #cccccc>${message}`
    - `#!xml <red>Hello <blue>world</blue>!</red>`
    - `#!xml <rainbow>Some colors for you`

There are also few self-contained tags, that don't require closing ones. They can also take arguments
in the same way to previous ones.

Few examples:
- `#!xml <lang 'item.minecraft.diamond'>`
- `#!xml <key 'key.jump'>`
## List of available tags

Here is list of all default tags available. Other mods can add new or limit usage
of existing ones, so not every might work in yours case.

### Colors

!!! note inline end

      This tag should be closed.

By default, there are multiple tags representing colors. They use their vanilla name or (additional aliases).

The current list includes  `#!xml <yellow>`, `#!xml <dark_blue>`, `#!xml <dark_purple>`, `#!xml <gold>`, `#!xml <red>`, `#!xml <aqua>`,
`#!xml <gray>`, `#!xml <light_purple>`, `#!xml <white>`, `#!xml <dark_gray>`, `#!xml <green>`, `#!xml <dark_green>`, `#!xml <blue>`,
`#!xml <dark_aqua>`, `#!xml <dark_green>`, `#!xml <black>`

There is also a universal `#!xml <color [VALUE]>`, `#!xml <color value:[VALUE]>`, `#!xml <c [VALUE]>` and `#!xml <c value:[VALUE]>` tags, in which you can replace `[VALUE]` with vanilla color name
or an rgb color starting with `#` (for example `#!xml <color #AABBCC>`)

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
Tag definitions:  `#!xml <click type:[TYPE] value:'[VALUE]'>`, `#!xml <click [TYPE] '[VALUE]'>` or direct shortcut tags.

There are few available actions:

- `#!xml <click open_url value '[VALUE]'>`/`#!xml <open_url '[VALUE]'>`/`#!xml <url '[VALUE]'>` - Opens the provided url
- `#!xml <click run_command '[VALUE]'>`/`#!xml <run_cmd '[VALUE]'>` - Runs a command as the player
- `#!xml <click suggest_command '[VALUE]'>`/`#!xml <suggest_command '[VALUE]'>`/`#!xml <cmd '[VALUE]'>` - Suggests a command to the player
- `#!xml <click copy_to_clipboard '[VALUE]'>`/`#!xml <copy_to_clipboard '[VALUE]'>`/`#!xml <copy '[VALUE]'>` - Copies text to the clipboard
- `#!xml <click change_page '[VALUE]'>`/`#!xml <change_page '[VALUE]'>`/`#!xml <page '[VALUE]'>` - Changes the page in a book

`[VALUE]` needs to be replaced with targeted value, for example `gamemode creative`

### Hover

Hover tag allows adding simple hover on text. It can be used to display additional information.

!!! note inline end

      This tag should be closed.

- `#!xml <hover show_text '[VALUE]'>`/`#!xml <hover '[VALUE]'>` - Adds a simple text hover (`[VALUE]` uses the same formatting as rest)
- `#!xml <hover show_item '[VALUE]'>` - Adds a simple ItemStack hover (`'[VALUE]'` is item in sNBT format)
- `#!xml <hover show_entity [type] [UUID] 'Display Name'>` - Adds an entity hover.
  (The colon (` `) in the entity type needs to be replaced with `\ `.
  For example, `#!xml <hover show_entity minecraft\ bee [UUID] 'Display Name'>`)

### Fonts

!!! note inline end

      This tag should be closed.

This tag allows you to change font to any build in one or one provided by resource pack.

You can use it by simply adding `#!xml <font '[VALUE]'>`, where `'[VALUE]'` is just a font name.
Minecraft has 3 build-in fonts  `default`, `uniform` and `alt`.

### Inserting

!!! note inline end

      This tag should be closed.

This tag creates a clickable text, that inserts its value at the end of player's chat message.

You can use it by writing `#!xml <insert '[VALUE]'>`, where `'[VALUE]'` inserted text (should be wrapped in `'`).

### Translations

!!! note inline end

    This tag is self containing, so it doesn't contain a closing tag.

Translations tag allows you to insert a text from a lang file (including ones parsed on servers by some mods).

You use it with `#!xml <lang [key] [optional arg 1] [optional arg 1] ...>` or `#!xml <lang key:[key] fallback:[fallback] [optional arg 1] [optional arg 1] ...>`
where `[key]` is a translation key, optional/key only `[fallback]` is used for keys missing their client side translations
and `[optional arg X]` are optional, fully formatted arguments you can pass.

### Control keys

!!! question inline end "Control Keys"

    You can find a list of control keys on the [Minecraft Wiki](https //minecraft.wiki/w/Controls#Configurable_controls)

!!! note inline end

    This tag is self containing, so it doesn't contain a closing tag.

This tag allows you to add information about player control keys, with respecting of theirs configuration.

You can use it with `#!xml <keybind '[VALUE]'>` or `#!xml <keybind value:'[VALUE]'>`, where `'[VALUE]'` is a control key used.

### Gradients

This tag allows you to add gradients to the text. However, it has multiple limitation that can
block its usage. Currently, you can't use dynamic values (translations, control keys, placeholders, etc)
within them, as they require static text.

There 2 types of gradients:
- `#!xml <gradient (type:[type]) [color 1] [color 2] ...>`/`#!xml <gr (type:[type]) [color 1] [color 2] ...>` - I can take multiple colors to move between them
  smoothly. You can replace the optional `(type:[type])` with `type:oklab` (default), `type:hvs` or `type:hard` to change how thge gradient is handled
- `#!xml <hard_gradient [color 1] [color 2] ...>`/`#!xml <hgr [color 1] [color 2] ...>` - I can take multiple colors to move between them
  without mixing them.
- `#!xml <rainbow [frequency] [saturation] [offset]>`/`<rainbow f:[frequency] s:[saturation] o:[offset]>`/`#!xml <rb [...]>` - It's simple rainbow gradient. All arguments are
  optional (`#!xml <ranbow>` is still valid) and they take numbers between 0 and 1 (`0.3` for example)

### Clear (2.1.3+)

!!! note inline end

      This tag should be closed.

This tag allows you to clear any formatting within this tag, without leaving any visible tags. It also
works with placeholders, which gives a bit more flexibility.

This tag can work without arguments making it clear all formatting or with them limiting clearing to selected types.

Examples:
- `#!xml <clear>` - Removes all formatting, leaving only text.
- `#!xml <clear hover>` - Removes all hovers.
- `#!xml <clear hover color>` - Removes all hovers and colors.

Supported arguments  `color`, `bold`, `italic`, `strikethrough`, `underline`, `hover`, `click`,:`insertion`, `font`, `all`.