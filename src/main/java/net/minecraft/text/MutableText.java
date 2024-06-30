package net.minecraft.text;

import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MutableText implements Text {
    private Style style = Style.EMPTY;
    private List<Text> siblings = new ArrayList<>();


    public MutableText formatted(Formatting... formattings) {
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
        return this;
    }

    @Override
    public MutableText copy() {
        return this;
    }

    @Override
    public String getString() {
        return "";
    }

    @Override
    public TextContent getContent() {
        return null;
    }

    @Override
    public List<Text> getSiblings() {
        return null;
    }

    public MutableText append(Text mutableText) {
        this.siblings.add(mutableText);
        return this;
    }

    public void styled(Function<Style, Style> styled) {
        this.setStyle(styled.apply(this.getStyle()));
    }

    public Text withColor(int i) {
        this.setStyle(this.getStyle().withColor(i));
        return this;
    }
}
