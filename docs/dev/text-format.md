# Using Simplified Text Format/TextParser

[*You can read about the format here!*](/user/text-format)

Usage of TextParser is simple and really customisable. You just need to import `eu.pb4.placeholders.api.TextParserUtils`
and call static `formatText` method for admin provided configs or `formatTextSafe` for player provided ones.
They both take only one String argument and output a Text object.

Example

===+ "Java"

    ```java
    String inputString = "<red>Hello <rb>World</rb>!"
    
    Text output = TextParserUtils.parseText(inputString);
    ```

=== "Kotlin"

    ```kotlin
    val inputString = "<red>Hello <rb>World</rb>!"

    val output = TextParserUtils.parseText(inputString);
    ```

## Parsing with only selected ones

If you want to only use selected tags, you can simply get map of all with `TextParserV1.DEFAULT.getTags()`.
Then you just use them with `TextParserUtils.parseText(String, TextParserV1.TagParserGetter)`.
