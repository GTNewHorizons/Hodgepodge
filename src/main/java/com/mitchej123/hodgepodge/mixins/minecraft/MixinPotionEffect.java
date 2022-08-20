package com.mitchej123.hodgepodge.mixins.minecraft;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionEffect.class)
public class MixinPotionEffect {

    @Shadow
    int potionID;

    /**
     * @author [com]buster
     * @reason Fix any incoming potion IDs that have negative values (and were probably incorrectly parsed earlier).
     */
    @Inject(method = "<init>(IIIZ)V", at = @At("RETURN"))
    public void primaryConstructor(
            int p_i1576_1_, int p_i1576_2_, int p_i1576_3_, boolean p_i1576_4_, CallbackInfo ci) {
        this.potionID = p_i1576_1_ & 0xff;
    }

    /**
     * @author [com]buster
     * @reason Force treat the potion id as an unsigned byte (to allow IDs 128-255)
     */
    @Overwrite()
    public static PotionEffect readCustomPotionEffectFromNBT(NBTTagCompound tag) {
        int potion = ((int) tag.getByte("Id")) & 0xff;
        if (potion >= 0 && potion < Potion.potionTypes.length && Potion.potionTypes[potion] != null) {
            byte amplifier = tag.getByte("Amplifier");
            int duration = tag.getInteger("Duration");
            boolean ambient = tag.getBoolean("Ambient");
            return new PotionEffect(potion, duration, amplifier, ambient);
        } else {
            return null;
        }
    }
}
