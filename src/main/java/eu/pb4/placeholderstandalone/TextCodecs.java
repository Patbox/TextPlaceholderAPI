package eu.pb4.placeholderstandalone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
public class TextCodecs {
    public static final Codec<Identifier> IDENTIFIER_CODEC = Codec.STRING.xmap(Identifier::of, Identifier::toString);
    public static final Codec<TextColor> COLOR_CODEC = Codec.STRING.flatXmap(TextColor::parse, (x) -> DataResult.success(x.name()));
    public static final Codec<HoverEvent<?>> HOVER_EVENT_CODEC =  Codec.unit(null);
    public static final Codec<ClickEvent> CLICK_EVENT_CODEC =  RecordCodecBuilder.create(instance -> instance.group(
            Codec.stringResolver(x -> x.name().toLowerCase(Locale.ROOT), x -> ClickEvent.Action.valueOf(x.toUpperCase(Locale.ROOT)))
                    .fieldOf("action").forGetter(ClickEvent::action),
            Codec.STRING.fieldOf("value").forGetter(ClickEvent::value)
    ).apply(instance, ClickEvent::new));

    public static final MapCodec<Style> STYLE_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        nullableCodec(COLOR_CODEC, "color").forGetter(Style::color),
        nullableCodec(Codec.BOOL, "italic").forGetter(Style::italic),
        nullableCodec(Codec.BOOL, "bold").forGetter(Style::bold),
        nullableCodec(Codec.BOOL, "underlined").forGetter(Style::underlined),
        nullableCodec(Codec.BOOL, "strikethrough").forGetter(Style::strikethrough),
        nullableCodec(Codec.BOOL, "obfuscated").forGetter(Style::obfuscated),
        nullableCodec(HOVER_EVENT_CODEC, "hoverEvent").forGetter(Style::hoverEvent),
        nullableCodec(CLICK_EVENT_CODEC, "clickEvent").forGetter(Style::clickEvent),
        nullableCodec(Codec.STRING, "insertion").forGetter(Style::insertion),
        nullableCodec(IDENTIFIER_CODEC, "font").forGetter(Style::font)
    ).apply(instance, Style::new));



    @SuppressWarnings("unchecked")
    public static final MapCodec<TextContent> CONTENT_CODEC =
            (MapCodec<TextContent>) (Object) Codec.STRING.xmap(PlainTextContent.Literal::new, PlainTextContent.Literal::string).fieldOf("text");

    public static final Codec<Text> RAW_CODEC = Codec.recursive("text", (self) -> RecordCodecBuilder.create(instance -> instance.group(
            CONTENT_CODEC.forGetter(Text::getContent),
            STYLE_CODEC.forGetter(Text::getStyle),
            self.listOf().optionalFieldOf("extra", List.of()).forGetter(Text::getSiblings)
    ).apply(instance, MutableText::new)));

    public static final Codec<Text> CODEC = Codec.withAlternative(RAW_CODEC, Codec.STRING, Text::literal);

    public static <B, T> MapCodec<@Nullable T> nullableCodec(Codec<T> codec, String field) {
        return codec.optionalFieldOf(field).xmap(x -> x.orElse(null), Optional::ofNullable);
    }

}
