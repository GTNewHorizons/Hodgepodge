package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.potion.Potion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(Potion.class)
public class MixinPotion {
    /*
      static <clinit>()V
       L0
        LINENUMBER 23 L0
        BIPUSH 32
        ANEWARRAY net/minecraft/potion/Potion
        PUTSTATIC net/minecraft/potion/Potion.potionTypes : [Lnet/minecraft/potion/Potion;
    */
    /**
     * @author eigenraven
     * @reason To replace multiple mods using reflection hacks making the array larger after its construction, incompatible with Java 12.
     */
    @ModifyConstant(
            method = "Lnet/minecraft/potion/Potion;<clinit>()V",
            slice =
                    @Slice(
                            from =
                                    @At(
                                            value = "FIELD",
                                            target =
                                                    "Lnet/minecraft/potion/Potion;potionTypes:[Lnet/minecraft/potion/Potion;",
                                            shift = At.Shift.BY,
                                            by = -3),
                            to =
                                    @At(
                                            value = "FIELD",
                                            target =
                                                    "Lnet/minecraft/potion/Potion;potionTypes:[Lnet/minecraft/potion/Potion;")),
            constant = @Constant(intValue = 32),
            require = 1)
    private static int potionTypesArrayLength(int original) {
        return 256;
    }
}
