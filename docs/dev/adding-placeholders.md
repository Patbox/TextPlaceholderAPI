# Adding placeholders
Creation of new placeholders is simple. You just need to import `eu.pb4.placeholders.api.Placeholders` 
and call static `register` method. You only need to provide 2 arguments:

- Identifier with your mod id as namespace and path as argument name (with one additional limitation being not allowed to use `/` in it).
- A function (in form of lambda for example) that takes PlaceholderContext and nullable string argument, returns PlaceholderResult,

Example
```
Placeholders.register(
         new Identifier("example", "placeholder"),
         (ctx, arg) -> PlaceholderResult.value(new LiteralText("Hello World!"))
);
```

## Using the context
PlaceholderContext object passed to placeholder contains allows getting player (if exist), server and argument value.
It also includes few methods for checking if they are present. 

Here is example for player only placeholder
```
Placeholders.register(new Identifier("player", "displayname"), (ctx, arg) -> {
    if (ctx.hasPlayer()) {
        return PlaceholderResult.value(ctx.getPlayer().getDisplayName());
    } else {
        return PlaceholderResult.invalid("No player!");
    }
});
```

You can also add an argument to your placeholder, which removes requirement 
of mostly repeated placeholders and allows degree of customisation.
Argument itself is a string, so you can parse it in any way.
```
PlaceholderAPI.register(new Identifier("server", "name_from_uuid"), (ctx, arg) -> {
    if (arg != null) {
        return PlaceholderResult.value(ctx.server().getUserCache().getByUuid(UUID.fromString(arg)).get().getName()));
    } else {
        return PlaceholderResult.invalid("No argument!");
    }
});
```

## Returning correct value
Placeholders need to return instance of PlaceholderResult. It can be created by usage of provided static methods on this class.

If it was successful:

* `PlaceholderResult.value(Text text)` - Creates a value with text
* `PlaceholderResult.value(String text)` - Creates a value from string, by parsing it with TextParser

If it was invalid (no player or argument for example):

* `PlaceholderResult.invalid()` - Creates simple invalid result
* `PlaceholderResult.invalid(String reason)` - Creates invalid result with a reason, 
  which is currently unused, but can be implemented by other parsers