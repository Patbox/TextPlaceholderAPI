# Using Simplified Text Format/TextParser
[*You can read about format here!*](/user/text-format)

Usage of TextParser is simple and really customisable. You just need to import `eu.pb4.placeholders.TextParser`
and call static `parse` method for admin provided configs or `parseSafe` for player provided ones.
They both take only one String argument and output a Text object.

Example
```
String inputString = "<red>Hello <rb>World</rb>!"

Text output = TextParser.parse(inputString); // Text output = TextParser.parseSafe(inputString);
```

## Parsing with only selected ones
If you want to only use selected tags, you can simply get map of all with `TextParser.getRegisteredTags()` 
or `TextParser.getRegisteredSafeTags()`. Then you can copy these to your own Map with String keys 
and `TextParser.TextFormatterHandler` values. 
Then you just use them with `TextParser.parse(String, Map<String, TextFormatterHandler>)`.

Example
```
String inputString = "<red>Hello <blue>World</blue>!"

Map<String, TextParser.TextFormatterHandler> tags = Map.of("red", TextParser.getRegisteredTags().get("red"),
                                                        "blue", TextParser.getRegisteredTags().get("yellow") // renames works too!
                                                    );

Text output = TextParser.parse(inputString, tags);
```