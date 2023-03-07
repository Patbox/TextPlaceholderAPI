# Text Nodes and Node Parsers

## What are TextNodes?
It is extensible representation of parsed text, allowing for simpler and more compatible parsing
without losing vanilla compatibility. It can be used for templating, which can minimize time 
required for parsing static text multiple times for placeholder insertion.
There are 2 main types of TextNodes.

### Value Text Nodes
This type of TextNode contains any text or defining information that isn't dependent on formatting.
It is the lowest level on which parsers generally operate.

You can create your own custom one by implementing `TextNode` interface.

===+ "Java"
```java
public record DirectTextNode(Text text) implements TextNode {
    @Override
    public Text toText(ParserContext context, boolean removeBackslash) {
        return this.text;
    }
}
```

The context is used mostly to allow for dynamic templating/values (for example placeholder context).
The `removeBackslash` option defines whatever LiteralNode should remove `\` symbol while 
it's used for escaping formatting.

Examples:

- LiteralNode - Used for direct text, requires special parsing by parsers,
- TranslationNode - Used for translated text, also requires special parsing,
- DirectTextNode - Allows you to insert non-transformable text, can be used for static placeholders, 
- PlaceholderNode (Internal) - Used for representing parsed placeholder without fetching it's final value.

### Parent Text Nodes
This type is used for joining multiple Value/Parent Text Nodes into single object. Additionally, 
they are used to add any type of formatting like colors, hovers or fonts. Most of builtin 
ParentNodes do single type of formatting.

You can create your own by extending `ParentNode` class or implementing `ParentTextNode` interface.

===+ "Java"
```java
public final class ColorNode extends ParentNode {
    private final TextColor color;

    public ColorNode(TextNode[] children, TextColor color) {
        super(children);
        this.color = color;
    }

    @Override
    protected Text applyFormatting(MutableText out, ParserContext context) {
        return out.setStyle(out.getStyle().withColor(this.color));
    }

    @Override
    public ParentTextNode copyWith(TextNode[] children) {
        return new ColorNode(children, this.color);
    }

    // This one should be only override if you have dynamic sub-values, like HoverNode
    @Override
    public ParentTextNode copyWith(TextNode[] children, NodeParser parser) {
        return new ColorNode(children, this.color);
    }
}
```

Examples:

- ParentNode - Most basic parent node, used purely for grouping elements,
- HoverNode - Adds HoverEvent to text,
- GradientNode - Allows inputting gradients for final text, without breaking parsing before.

## Node Parsers
Simply put, Node Parsers are parsers operating on Text Nodes. They use them both as input and output, 
allowing them to easily stack with other Node Parsers. They need to implement a `NodeParser` interface.

To use Node Parsers, you first need to get instance of it and then invoke one of provided method.

For example:

===+ "Java"
```java
public class Example {

    // With player context
    public static void exampleContext(ServerPlayerEntity player) {
        NodeParser parser = NodeParser.merge(TextParserV1.DEFAULT, Placeholders.DEFAULT_PLACEHOLDER_GETTER);

        TextNode output = parser.parseNode("<rb>Hello %player:name%");
        // or
        TextNode output = parser.parseNode(TextNode.of("<rb>Hello %player:name%"));
        // or (only way before 2.0.0-beta.4)
        TextNode output = TextNode.asSingle(parser.parseNodes(TextNode.of("<rb>Hello %player:name%")));

        Text text = output.toText(PlaceholderContext.of(player));
        // or
        Text text = output.toText(PlaceholderContext.of(player).asParserContext());
        // or
        Text text = output.toText(ParserContext.of().with(PlaceholderContext.KEY, PlaceholderContext.of(player)));
        // or (only way before 2.0.0-beta.4)
        Text text = output.toText(PlaceholderContext.of(player).asParserContext(), true);
    }

    // Without context
    public static void example() {
        NodeParser parser = NodeParser.merge(TextParserV1.DEFAULT, Placeholders.DEFAULT_PLACEHOLDER_GETTER);

        TextNode output = parser.parseNode("<rb>Hello user!");
        
        Text text = output.toText();
        // or
        Text text = output.toText(ParserContext.of());
        // or (only way before 2.0.0-beta.4)
        Text text = output.toText(ParserContext.of(), true);
    }
}
```

Text Placeholder API comes with multiple builtin parsers:

- TextParserV1 [(See more here!)](/dev/text-format) - Tag based parser for user input,
- MarkdownLiteParserV1 - Minimalistic Markdown parser with only vanilla compatible formatting,
- LegacyFormattingParser - Simple parser adding support for legacy (&) formatting,
- PatternPlaceholderParser - Backend parser used by placeholder implementation. Added as NodeParser with 2.0.0-pre.4.

## Custom Node Parsers
Implementing custom Node Parsers might be tricky. But the simplest one boils down to implementing NodeParser.

Default implementation should support parsing `LiteralNode`, `TranslatedNode` and `ParentNode` 
to be considered functional.

===+ "Java"
```java
public record ExampleParser() implements NodeParser {
    public TextNode[] parseNodes(TextNode input) {
        if (input instanceof LiteralNode node) {
            return TextNode.array(new LiteralNode(node.value().replace("<3", "❤️")));
        } else if (input instanceof TranslatedNode node) {
            var args = new ArrayList<>();
            for (var arg : node.args()) {
                args.add(arg instanceof TextNode argNode ? this.parseNode(argNode) : arg);
            }
            
            return TextNode.array(new TranslatedNode(node.key(), args.toArray()));
        } else if (input instanceof ParentTextNode node) {
            var children = new ArrayList<TextNode>();

            for (var child : parentNode.getChildren()) {
                children.add(this.parseNode(child));
            }

            return TextNode.array(parentNode.copyWith(out.toArray(new TextNode[0]), this));
        }
        
        return TextNode.array(input);
    }
}
```

Then it can be used directly just like any other builtin NodeParser.