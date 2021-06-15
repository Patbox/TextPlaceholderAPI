# About tags/TextParser
TextParser is a small utility for allowing user to create Text Components in simpler way.
It works in similar way to HTML, however it's structure has some differences.
Tags have structure of `<tag_name>` or `<tag_name:argument>`. Most of tags needs to be closed by using `</tag_name>`,
but there are exceptions from this (which is noted below). Argument can be any string, but it should be
wrapped with `'` symbol (otherwise it might break way more). Additionally, all `:` within it should be escaped with `\` symbol (`\:`)

# List of formatting tags
- `<yellow>` - Changes text color to yellow,
- `<dark_blue>` - Changes text color to dark blue,
- `<dark_purple>` - Changes text color to dark purple,
- `<gold>` - Changes text color to gold,
- `<red>` - Changes text color to red,
- `<aqua>` - Changes text color to aqua,
- `<gray>` - Changes text color to gray,
- `<light_purple>` - Changes text color to light purple,
- `<white>` - Changes text color to white,
- `<dark_gray>` - Changes text color to dark gray,
- `<green>` - Changes text color to green,
- `<dark_green>` - Changes text color to dark green,
- `<blue>` - Changes text color to blue,
- `<dark_aqua>` - Changes text color to dark aqua,
- `<dark_green>` - Changes text color to dark green,
- `<black>` - Changes text color to black,
- `<color:[color name or rgb value]>` / `<c:[arg]>` - Changes text color to selected vanilla color or rgb value with `#RRGGBB` format,

- `<strikethrough>` - Makes text strikethrough,
- `<underline>` - Underlines text,
- `<italic>` - Makes text italic,
- `<obfuscated>` - Obfuscates text (matrix effect),
- `<bold>` - Makes text bold,

- `<click:event_name:'value'>` - Adds click even to text (see vanilla [click events here](https://minecraft.fandom.com/wiki/Raw_JSON_text_format))
- `<hover:event_name:'value'>` - Adds hover information. Supported events:
  - `show_text` - Shows text: `<hover:show_text:'text with same formatting as rest'>`
  - `show_item` - Shows item: `<hover:show_item:'item as sNBT'>`
  - `show_entity` - Shows item: `<hover:show_entity:'entity type (: needs to be prefixed with \)':'uuid':'formatted text'>`

- `<insert:'value'>` - After clicking makes player insert text to their message,

- `<font:font_name>` - Changes font to selected one (can be from vanilla or resource pack),
- `<lang:lang_value:'optional arg 1':'optional arg 2'...>` - Adds value depending on your lang file. Doesn't need closing tag,
- `<key:key_name>` - Adds name of clientside control key (see [values here](https://minecraft.fandom.com/wiki/Controls#Configurable_controls)). Doesn't need closing tag,

- `<rainbow:[frequency]:[saturation]:[offset]>` / `<rb:[...]>` - Makes text within it have rainbow colors (supports inner formatting). All arguments are optional (`<ranbow>` is still valid),
- `<gradient:[color 1]:[color 2]:...>` / `<gr:[...]>` - Creates a gradient, can have multiple main colors,

- `<reset>` - Closes all previous tags,
