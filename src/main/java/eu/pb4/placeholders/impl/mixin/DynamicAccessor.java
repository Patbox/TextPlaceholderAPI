package eu.pb4.placeholders.impl.mixin;

import com.mojang.datafixers.util.Either;
import net.minecraft.component.type.ProfileComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.UUID;

@Mixin(ProfileComponent.Dynamic.class)
public interface DynamicAccessor {
    @Invoker("<init>")
    static ProfileComponent.Dynamic createDynamic(Either<String, UUID> nameOrId) {
        throw new UnsupportedOperationException();
    }
}
