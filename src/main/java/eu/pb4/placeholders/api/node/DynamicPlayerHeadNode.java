package eu.pb4.placeholders.api.node;

import eu.pb4.placeholders.api.ParserContext;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.text.Text;
import net.minecraft.text.object.PlayerTextObjectContents;

import java.util.UUID;

public record DynamicPlayerHeadNode(TextNode name, boolean hat, Type type) implements TextNode {
    @Override
    public Text toText(ParserContext context, boolean removeBackslashes) {
        var val = this.name.toText(context).getString();

        if (type == Type.UUID || type == Type.EITHER) {
            try {
                return Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(UUID.fromString(val)), hat));
            } catch (Throwable e) {
                // ignore
            }
        }

        if (type == Type.NAME || type == Type.EITHER) {
            try {
                return Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(val), hat));
            } catch (Throwable e) {
                // ignore
            }
        }

        return Text.object(new PlayerTextObjectContents(ProfileComponent.ofDynamic(""), hat));
    }

    public enum Type {
        UUID,
        NAME,
        EITHER
    }
}
