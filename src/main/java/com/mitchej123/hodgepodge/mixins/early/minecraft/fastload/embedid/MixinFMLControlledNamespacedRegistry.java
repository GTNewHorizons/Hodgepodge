package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.embedid;

import com.mitchej123.hodgepodge.mixins.interfaces.EmbedToggle;
import net.minecraft.util.RegistryNamespaced;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.TypeSettable;

import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;

@Mixin(value = FMLControlledNamespacedRegistry.class)
public abstract class MixinFMLControlledNamespacedRegistry<I> extends RegistryNamespaced implements EmbedToggle {

    @Shadow(remap = false)
    @Final
    private Class<I> superType;

    @Override
    public void hodgepodge$setUseEmbed(boolean useEmbed) {
        ((EmbedToggle) underlyingIntegerMap).hodgepodge$setUseEmbed(useEmbed);
    }

    @Inject(method = "<init>", at = @At("RETURN"), remap = false)
    private void hodgepodge$setType(String optionalDefault, int maxIdValue, int minIdValue, Class<I> type,
            char discriminator, CallbackInfo ci) {
        ((TypeSettable) this.underlyingIntegerMap).hodgepodge$setType(this.superType);
    }

    @Inject(
            method = "set",
            at = @At(
                    value = "FIELD",
                    target = "Lcpw/mods/fml/common/registry/FMLControlledNamespacedRegistry;underlyingIntegerMap:Lnet/minecraft/util/ObjectIntIdentityMap;",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER))
    private void hodgepodge$setType(FMLControlledNamespacedRegistry<I> registry, CallbackInfo ci) {
        ((TypeSettable) this.underlyingIntegerMap).hodgepodge$setType(this.superType);
    }
}
