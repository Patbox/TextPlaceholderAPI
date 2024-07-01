package net.minecraft.text;

import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MutableText implements Text {
    private Style style = Style.EMPTY;
    private final List<Text> siblings = new ArrayList<>();
    private final TextContent contant;
    public MutableText(TextContent contant) {
        this.contant = contant;
    }

    public MutableText(TextContent contant, Style style, List<Text> siblings) {
        this.contant = contant;
        this.style = style;
        this.siblings.addAll(siblings);
    }

    public MutableText formatted(Formatting... formattings) {
        this.setStyle(this.getStyle().withFormatting(formattings));
        return this;
    }

    public Text withColor(int i) {
        this.setStyle(this.getStyle().withColor(i));
        return this;
    }
    public MutableText setStyle(Style style) {
        this.style = style;
        return this;
    }

    public Style getStyle() {
        return this.style;
    }

    @Override
    public MutableText copyContentOnly() {
        return new MutableText(this.contant).setStyle(this.style);
    }

    @Override
    public MutableText copy() {
        var c = copyContentOnly();
        c.siblings.addAll(this.siblings);
        return c;
    }

    @Override
    public String getString() {
        var b = new StringBuilder();
        b.append(this.contant.getString());
        this.siblings.forEach(x -> b.append(x.getString()));
        return b.toString();
    }

    @Override
    public TextContent getContent() {
        return this.contant;
    }

    @Override
    public List<Text> getSiblings() {
        return this.siblings;
    }

    public MutableText append(Text mutableText) {
        this.siblings.add(mutableText);
        return this;
    }

    public MutableText styled(Function<Style, Style> styled) {
        this.setStyle(styled.apply(this.getStyle()));
        return this;
    }
}
