# Parsing placeholders
There are few ways (and types) of placeholders you can parse with PlaceholderAPI. 
So depending on your use case some of these will be more useful than others.

## Parsing global placeholders
Parsing global placeholders is really simple, as long as you have access to ServerPlayerEntity
or MinecraftServer object. You just need to simply import `eu.pb4.placeholders.api.Placeholders` and call
`parseText`. This method will return fully parsed Text, which can be displayed to the user.

Example
```
Text message = Placeholders.parseText(textInput, PlaceholderContext.of(...));
```

Placeholders itself will use default formatting of `%category:placeholder%`. 
If you want to use other formatting for them (which is recommended), you can use
`parseText(Text, PlaceholderContext)` for `{category:placeholder}`.

## Parsing own/custom/predefined placeholders
If you want to parse your own placeholders, you can do this in 2 ways.

### Static placeholders
To parse static placeholders you need to create a Map with `String` as a key and `Text` as a value.
You also need a Pattern object (which can be taken from predefined ones). Then it's as simple as calling 
a `parsePredefinedText` static method on `PlaceholderAPI` class.

Example
```
ServerPlayerEntity player = something.getPlayer(); // MinecraftServer server = something.getServer()

Text inputText = Text.literal("Hello! ${player}");
Map<String, Text> placeholders = Map.of("player", Text.literal("You are a player!"));
Pattern pattern = Placeholders.PREDEFINED_PLACEHOLDER_PATTERN;

Text output = Placeholders.parseText(inputText, pattern, placeholders);
```

### Dynamic placeholders
In case where you want to parse placeholder with a context similar to global one, you need to
create a Map with `Identifier` as a key and `PlaceholderHandler` as a value (same as adding global ones).
You also will need a pattern object, which is the same as with static ones.

As opposite to global ones, you don't need to define namespace/category as it can default to minecraft one (for simpler user input).
Then you just parse it with `parseText(Text, PlaceholderContext, Pattern, PlaceholderGetter)`.

Example
```
ServerPlayerEntity player = something.getPlayer(); // MinecraftServer server = something.getServer()

Text inputText = new Text.literal("Hello! ${player blue}");
PlaceholderGetter placeholders = (id) -> switch {
    case "player" -> (ctx) -> {
                                   if (ctx.hasPlayer()) {
                                       return PlaceholderResult.value(Text.literal("You are a player!")
                                                   .setStyle(Style.EMPTY.withColor(TextColor.parse(ctx.getArgument()))));
                                   } else {
                                       return PlaceholderResult.value(Text.literal("You are a server!")
                                                   .setStyle(Style.EMPTY.withColor(TextColor.parse(ctx.getArgument()))));
                                   }
                              });
}
Pattern pattern = Placeholders.PREDEFINED_PLACEHOLDER_PATTERN;

Text output = Placeholders.parseText(inputText, PlaceholderContext.of(player), pattern, placeholders);
```

### Preferred Patterns for static
PlaceholderAPI has few Patterns you can use, which are accessible as static objects on `Placeholders` class.

* `PREDEFINED_PLACEHOLDER_PATTERN` (`${placeholder}`) - works the best in most cases, doesn't collide with other ones.
* `ALT_PLACEHOLDER_PATTERN_CUSTOM` (`{placeholder}`) - second best, but have more chance of colliding with user formatting.

There are other ones, which usage is allowed, but they might work worse.

* `PLACEHOLDER_PATTERN_CUSTOM` (`%placeholder%`) - is the same as default one, but doesn't require `:`.
* `PLACEHOLDER_PATTERN` (`%category:placeholder%`) - used by default global placeholders (requires category).
* `PLACEHOLDER_PATTERN_ALT` (`{category:placeholder}`) - used as alternative formatting for global ones (requires category).

