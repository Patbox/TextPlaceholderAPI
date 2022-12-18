# Parsing placeholders

There are few ways (and types) of placeholders you can parse with PlaceholderAPI.
So depending on your use case some of these will be more useful than others.

## Placeholder Context

Placeholders should be provided with a context at all times.
The context allows the placeholder parser to inject placeholders that require an optional context, such as a player.

A `PlaceholderContext` is created using one of the `PlaceholderContext.of(...)` variants.
Depending on the objects passed, different placeholders may be available.

???+ info "`PlaceholderContext.of(...)` Variants"

    - `MinecraftServer`
        - May use placeholders that depend on the server
        - May use placeholders that depend on `ServerCommandSource` (Note: uses the command source
          from `MinecraftServer.getCommandSource()`)
    - `GameProfile`
        - May use placeholders that depend on the server
        - May use placeholders that depend on `ServerCommandSource` (Note: creates a new dummy command source at `(0,0,0)`)
        - May use placeholders that depend on `GameProfile`
    - `ServerPlayerEntity`
        - May use placeholders that depend on the server
        - May use placeholders that depend on `ServerCommandSource`
        - May use placeholders that depend on `ServerWorld`
        - May use placeholders that depend on `ServerPlayerEntity`
        - May use placeholders that depend on `Entity`
        - May use placeholders that depend on `GameProfile`
    - `ServerCommandSource`
        - May use placeholders that depend on the server
        - May use placeholders that depend on `ServerCommandSource`
        - May use placeholders that depend on `ServerWorld`
        - May use placeholders that depend on `ServerPlayerEntity` *only if the source has a player*
        - May use placeholders that depend on `Entity` *only if the source has an entity*
        - May use placeholders that depend on `GameProfile` *only if the source has a player*
    - `Entity`
        - May use placeholders that depend on the server
        - May use placeholders that depend on `ServerCommandSource`
        - May use placeholders that depend on `ServerWorld`
        - May use placeholders that depend on `ServerPlayerEntity` *only if the source has a player*
        - May use placeholders that depend on `Entity`
        - May use placeholders that depend on `GameProfile` *only if the source has a player*

## Parsing global placeholders

Parsing global placeholders is really simple, as long as you have access to ServerPlayerEntity
or MinecraftServer object. You just need to simply import `eu.pb4.placeholders.api.Placeholders` and call
`parseText`. This method will return fully parsed Text, which can be displayed to the user.

Example

===+ "Java"

    ```java
    Text message = Placeholders.parseText(textInput, PlaceholderContext.of(...));
    ```

=== "Kotlin"

    ```kotlin
    val message = Placeholders.parseText(textInput, PlaceholderContext.of(...))
    ```

Placeholders itself will use default formatting of `%category:placeholder%`.
If you want to use other formatting for them (which is recommended), you can use
`parseText(Text, PlaceholderContext, Pattern)`. Prefer those listed in [Preferred Patterns for static](#preferred-patterns-for-static).

## Parsing own/custom/predefined placeholders

If you want to parse your own placeholders, you can do this in 2 ways.

### Static placeholders

To parse static placeholders you need to create a Map with `String` as a key and `Text` as a value.
You also need a Pattern object (which can be taken from predefined ones). Then it's as simple as calling
a `parseText` static method on `PlaceholderAPI` class.

Example

===+ "Java"

    ```java title="StaticPlaceholders.java"
    --8<--
    docs/dev/code/StaticPlaceholders.java:static
    --8<--
    ```

===  "Kotlin"

    ```kotlin title="StaticPlaceholders.kt"
    --8<--
    docs/dev/code/kotlin/StaticPlaceholders.kt:static
    --8<--
    ```

### Dynamic placeholders

In case where you want to parse placeholder with a context similar to global one, you need to
create a Map with `Identifier` as a key and `PlaceholderHandler` as a value (same as adding global ones).
You also will need a pattern object, which is the same as with static ones.

As opposite to global ones, you don't need to define namespace/category as it can default to minecraft one (for simpler user input).
Then you just parse it with `parseText(Text, PlaceholderContext, Pattern, PlaceholderGetter)`.

Example

===+ "Java"

    ```java title="DynamicPlaceholders.java"
    --8<--
    docs/dev/code/DynamicPlaceholders.java:dynamic
    --8<--
    ```

===  "Kotlin"

    ```kotlin title="DynamicPlaceholders.kt"
    --8<--
    docs/dev/code/kotlin/DynamicPlaceholders.kt:dynamic
    --8<--
    ```

### Preferred Patterns for static

PlaceholderAPI has few Patterns you can use, which are accessible as static objects on `Placeholders` class.

- `#!java Placeholders.PREDEFINED_PLACEHOLDER_PATTERN` (`${placeholder}`) - works the best in most cases, doesn't collide with other ones.
- `#!java Placeholders.ALT_PLACEHOLDER_PATTERN_CUSTOM` (`{placeholder}`) - second best, but have more chance of colliding with user
  formatting.

There are other ones, which usage is allowed, but they might work worse.

- `#!java Placeholders.PLACEHOLDER_PATTERN_CUSTOM` (`%placeholder%`) - is the same as default one, but doesn't require `:`.
- `#!java Placeholders.PLACEHOLDER_PATTERN` (`%category:placeholder%`) - used by default global placeholders (requires category).
- `#!java Placeholders.PLACEHOLDER_PATTERN_ALT` (`{category:placeholder}`) - used as alternative formatting for global ones (requires
  category).

