package eu.pb4.placeholderstandalone;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.serialization.JsonOps;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import net.minecraft.server.network.ServerPlayerEntity;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Main {
    public static void main(String... args) {
        var parser = NodeParser.builder().quickText().globalPlaceholders().build();

        try {
            var input = Files.readString(Path.of("input.qtxt").toAbsolutePath());
            var node = TextNode.asSingle(parser.parseNode(input));
            var text = node.toText(PlaceholderContext.of(new ServerPlayerEntity("Patbox")));
            var gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

            Files.writeString(Path.of("output.json"), gson.toJson(TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).result().get()), StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
