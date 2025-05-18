package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.flatid;

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

@Mixin(FMLControlledNamespacedRegistry.class)
public abstract class MixinFMLControlledNamespacedRegistry<I> extends RegistryNamespaced {

    @Shadow
    @Final
    private Class<I> superType;

    @Inject(method = "<init>", at = @At("RETURN"))
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
