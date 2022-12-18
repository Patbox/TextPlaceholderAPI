# Adding placeholders

Creation of new placeholders is simple. You just need to import `eu.pb4.placeholders.api.Placeholders`
and call static `register` method. You only need to provide 2 arguments:

- Identifier with your mod id as namespace and path as argument name (with one additional limitation being not allowed to use `/` in it).
- A function (in form of lambda for example) that takes PlaceholderContext and nullable string argument, returns PlaceholderResult,

Example

===+ "Java"

    ```java
    Placeholders.register(
             new Identifier("example", "placeholder"),
             (ctx, arg) -> PlaceholderResult.value(Text.literal("Hello World!"))
    );
    ```

=== "Kotlin"

    ```kotlin
    Placeholders.register(Identifier("example", "placeholder")) { ctx, arg ->
        PlaceholderResult.value(Text.literal("Hello World!"))
    }
    ```

## Using the context

`PlaceholderContext` object passed to placeholder contains allows retrieving the server, the `ServerCommandSource`, the source world (if
exist), the source `ServerPlayerEntity` (if exist), the source `Entity` (if exists), and the source's `GameProfile` (if exists).

It also includes few methods for checking if they are present, such as `hasWorld()`, `hasPlayer()`, `hasGameProfile()`, and `hasEntity()`.

Here is example for a placeholder, which requires a player:

===+ "Java"

    ```java
    Placeholders.register(new Identifier("player", "displayname"), (ctx, arg) -> {
        if (!ctx.hasPlayer())
            return PlaceholderResult.invalid("No player!");

        return PlaceholderResult.value(ctx.getPlayer().getDisplayName());
    });
    ```

=== "Kotlin"

    ```kotlin
    Placeholders.register(Identifier("player", "displayname")) { ctx, args ->
        if (!ctx.hasPlayer())
            return PlaceholderResult.invalid("No player!")

        PlaceholderResult.value(ctx.player!!.displayName)
    }
    ```

## Arguments

You can also add an argument to your placeholder, which removes requirement
of mostly repeated placeholders and allows degree of customisation.
Argument itself is a string, so you can parse it in any way.

===+ "Java"

    ```java
    PlaceholderAPI.register(new Identifier("server", "name_from_uuid"), (ctx, arg) -> {
        if (arg == null)
            return PlaceholderResult.invalid("No argument!");

        UUID uuid = UUID.fromString(arg);
        GameProfile player = ctx.server().getUserCache().getByUuid(UUID.fromString(arg)).get()
        
        return PlaceholderResult.value(player.getName()));
    });
    ```

=== "Kotlin"

    ```kotlin
    PlaceholderAPI.register(Identifier("server", "name_from_uuid")) { ctx, arg ->
        if (arg == null)
            return PlaceholderResult.invalid("No argument!")

        val uuid = UUID.fromString(arg)
        val player = ctx.server().userCache.getByUuid(uuid).get()

        return PlaceholderResult.value(player.name)
    }
    ```

## Returning correct value

Placeholders need to return instance of PlaceholderResult. It can be created by usage of provided static methods on this class.

If it was successful:

- `#!java PlaceholderResult.value(Text text)` - Creates a value with text
- `#!java PlaceholderResult.value(String text)` - Creates a value from string, by parsing it with TextParser

If it was invalid (for example, no player or argument):

- `#!java PlaceholderResult.invalid()` - Creates simple invalid result
- `#!java PlaceholderResult.invalid(String reason)` -- Creates invalid result with a reason.
  The reason is returned as a response, but may be used further by other parsers.
