package eu.pb4.placeholderstandalone;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TextCodecs {
    public static final Text DEFAULT_SEPARATOR = Text.literal(", ").formatted(Formatting.GRAY);
    public static final Codec<Identifier> IDENTIFIER_CODEC = Codec.STRING.xmap(Identifier::of, Identifier::toString);
    public static final Codec<TextColor> COLOR_CODEC = Codec.STRING.flatXmap(TextColor::parse, (x) -> DataResult.success(x.name()));
    /*public static final Map<HoverEvent.Action<?>, MapCodec<?>> HOVER_CODECS = (Map<Class<NbtDataSource>, MapCodec<NbtDataSource>>) (Object)
            ImmutableMap.<Class<?>, MapCodec<?>>builder()
                    .put(BlockNbtDataSource.class, Codec.STRING.xmap(BlockNbtDataSource::new, BlockNbtDataSource::path).fieldOf("block"))
                    .put(EntityNbtDataSource.class, Codec.STRING.xmap(EntityNbtDataSource::new, EntityNbtDataSource::path).fieldOf("entity"))
                    .put(StorageNbtDataSource.class, IDENTIFIER_CODEC.xmap(StorageNbtDataSource::new, StorageNbtDataSource::path).fieldOf("storage"))
                    .build())*/
    public static final Codec<ClickEvent> CLICK_EVENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.stringResolver(x -> x.name().toLowerCase(Locale.ROOT), x -> ClickEvent.Action.valueOf(x.toUpperCase(Locale.ROOT)))
                    .fieldOf("action").forGetter(ClickEvent::action),
            Codec.STRING.fieldOf("value").forGetter(ClickEvent::value)
    ).apply(instance, ClickEvent::new));

    public static final Codec<Text> CODEC = Codec.lazyInitialized(
            () -> Codec.recursive("text", (self) -> Codec.either(Codec.STRING, RecordCodecBuilder.<Text>create(instance -> instance.group(
            TextCodecs.CONTENT_CODEC.forGetter(Text::getContent),
            TextCodecs.STYLE_CODEC.forGetter(Text::getStyle),
            self.listOf().optionalFieldOf("extra", List.of()).forGetter(Text::getSiblings)
    ).apply(instance, MutableText::new))).xmap(x -> x.right().orElseGet(() -> Text.literal(x.left().get())),
                    x -> x.getContent() instanceof PlainTextContent.Literal l && x.getSiblings().isEmpty() && x.getStyle().isEmpty() ? Either.left(l.string()) : Either.right(x))));
    public static final Codec<ItemStack> ITEM_STACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    IDENTIFIER_CODEC.fieldOf("id").forGetter(ItemStack::getItemId),
                    Codec.intRange(0, 99).optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
                    Codec.unboundedMap(IDENTIFIER_CODEC, Codec.PASSTHROUGH.xmap(
                            dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(),
                            object -> new Dynamic<>(JsonOps.INSTANCE, object))
                    ).optionalFieldOf("components", Map.of()).forGetter(ItemStack::getComponents)
            ).apply(instance, ItemStack::new)
    );
    public static final Codec<HoverEvent.Action<?>> HOVER_ACTION_TYPE_CODEC = Codec.stringResolver(
            Map.of(HoverEvent.Action.SHOW_ENTITY, "show_entity", HoverEvent.Action.SHOW_ITEM, "show_item", HoverEvent.Action.SHOW_TEXT, "show_text")::get,
            Map.of("show_entity", HoverEvent.Action.SHOW_ENTITY, "show_item", HoverEvent.Action.SHOW_ITEM, "show_text", HoverEvent.Action.SHOW_TEXT)::get
    );
    @SuppressWarnings("unchecked")
    public static final MapCodec<NbtDataSource> NBT_SOURCE_CODEC = new ClassTypeMapCodec<>((Map<Class<NbtDataSource>, MapCodec<NbtDataSource>>) (Object)
            ImmutableMap.<Class<?>, MapCodec<?>>builder()
                    .put(BlockNbtDataSource.class, Codec.STRING.xmap(BlockNbtDataSource::new, BlockNbtDataSource::path).fieldOf("block"))
                    .put(EntityNbtDataSource.class, Codec.STRING.xmap(EntityNbtDataSource::new, EntityNbtDataSource::path).fieldOf("entity"))
                    .put(StorageNbtDataSource.class, IDENTIFIER_CODEC.xmap(StorageNbtDataSource::new, StorageNbtDataSource::path).fieldOf("storage"))
                    .build());

    public static <B, T> MapCodec<@Nullable T> nullableCodec(Codec<T> codec, String field) {
        return codec.optionalFieldOf(field).xmap(x -> x.orElse(null), Optional::ofNullable);
    }    @SuppressWarnings({"rawtypes", "unchecked"})
    private static final Map<HoverEvent.Action<?>, MapCodec<HoverEvent>> ACTION_TO_VALUE_CODEC =
            Map.of(HoverEvent.Action.SHOW_ENTITY, RecordCodecBuilder.<HoverEvent.EntityContent>create(instance -> instance.group(
                                    IDENTIFIER_CODEC.xmap((x) -> new EntityType(x), EntityType::id).fieldOf("type").forGetter(HoverEvent.EntityContent::entityType),
                                    Codec.STRING.xmap(UUID::fromString, UUID::toString).fieldOf("id").forGetter(HoverEvent.EntityContent::uuid),
                                    nullableCodec(CODEC, "name").forGetter(HoverEvent.EntityContent::text)
                            ).apply(instance, HoverEvent.EntityContent::new)),
                            HoverEvent.Action.SHOW_ITEM, ITEM_STACK_CODEC.xmap(HoverEvent.ItemStackContent::new, HoverEvent.ItemStackContent::stack),
                            HoverEvent.Action.SHOW_TEXT, CODEC).entrySet().stream()
                    .map(x -> Map.entry(x.getKey(),
                            MapCodec.assumeMapUnsafe(Codec.withAlternative((Codec<Object>) x.getValue().fieldOf("contents").codec(), x.getValue().fieldOf("value").codec()).xmap(b -> new HoverEvent(x.getKey(), b), HoverEvent::object))
                    ))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public static final class ClassTypeMapCodec<T> extends MapCodec<T> {

        private final Map<Class<T>, MapCodec<T>> map;

        public ClassTypeMapCodec(Map<Class<T>, MapCodec<T>> map) {
            this.map = map;
        }

        @Override
        public <T1> Stream<T1> keys(DynamicOps<T1> ops) {
            return Stream.empty();
        }

        @Override
        public <T1> DataResult<T> decode(DynamicOps<T1> ops, MapLike<T1> input) {
            for (var codec : this.map.values()) {
                var res = codec.decode(ops, input);
                if (res.isSuccess()) {
                    return res;
                }
            }


            return DataResult.error(() -> "Not valid content type!");
        }

        @Override
        public <T1> RecordBuilder<T1> encode(T input, DynamicOps<T1> ops, RecordBuilder<T1> prefix) {
            var codec = this.map.get(input.getClass());
            if (codec != null) {
                codec.encode(input, ops, prefix);
            }

            return prefix;
        }
    }    @SuppressWarnings("unchecked")
    public static final Codec<HoverEvent<?>> HOVER_EVENT_CODEC = HOVER_ACTION_TYPE_CODEC.dispatch("action", HoverEvent::getAction, x -> (MapCodec<? extends HoverEvent<?>>) (Object) ACTION_TO_VALUE_CODEC.get(x));

    @SuppressWarnings("unchecked")
    public static final MapCodec<TextContent> CONTENT_CODEC = new ClassTypeMapCodec<>((Map<Class<TextContent>, MapCodec<TextContent>>) (Object) ImmutableMap.<Class<?>, MapCodec<?>>builder()
            .put(PlainTextContent.Literal.class, Codec.STRING.xmap(PlainTextContent.Literal::new, PlainTextContent.Literal::string).fieldOf("text"))
            .put(KeybindTextContent.class, Codec.STRING.xmap(KeybindTextContent::new, KeybindTextContent::key).fieldOf("keybind"))
            .put(TranslatableTextContent.class, RecordCodecBuilder.<TranslatableTextContent>mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("translate").forGetter(TranslatableTextContent::key),
                    nullableCodec(Codec.STRING, "fallback").forGetter(TranslatableTextContent::fallback),
                    Codec.withAlternative((Codec<Object>) (Object) CODEC, Codec.PASSTHROUGH.xmap(
                            dynamic -> dynamic.convert(JavaOps.INSTANCE).getValue(),
                            object -> new Dynamic<>(JavaOps.INSTANCE, object))
                    ).listOf().xmap(List::toArray, List::of).optionalFieldOf("with", new Object[0]).forGetter(TranslatableTextContent::args)
            ).apply(instance, TranslatableTextContent::new)))
            .put(NbtTextContent.class, RecordCodecBuilder.<NbtTextContent>mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("nbt").forGetter(NbtTextContent::path),
                    Codec.BOOL.optionalFieldOf("interpret", false).forGetter(NbtTextContent::shouldInterpret),
                    CODEC.optionalFieldOf("separator").forGetter(NbtTextContent::separator),
                    NBT_SOURCE_CODEC.forGetter(NbtTextContent::dataSource)
            ).apply(instance, NbtTextContent::new)))
            .put(ScoreTextContent.class, RecordCodecBuilder.<ScoreTextContent>mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("name").forGetter(ScoreTextContent::name),
                    Codec.STRING.fieldOf("objective").forGetter(ScoreTextContent::objective)
            ).apply(instance, ScoreTextContent::new)))
            .put(SelectorTextContent.class, RecordCodecBuilder.<SelectorTextContent>mapCodec(instance -> instance.group(
                    Codec.STRING.fieldOf("selector").forGetter(SelectorTextContent::pattern),
                    CODEC.optionalFieldOf("separator").forGetter(SelectorTextContent::separator)
            ).apply(instance, SelectorTextContent::new)))
            .build());

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






}
