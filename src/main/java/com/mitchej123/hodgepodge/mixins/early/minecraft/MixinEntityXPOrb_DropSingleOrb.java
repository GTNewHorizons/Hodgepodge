package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.entity.item.EntityXPOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityXPOrb.class)
public class MixinEntityXPOrb_DropSingleOrb {

    /**
     * @author nimuck
     * @reason Drop a single xp orb instead of subdividing into multiple
     */
    @Overwrite
    public static int getXPSplit(int expValue) {
        if (expValue <= 0) return 1;
        return expValue > 32767 ? 32767 : expValue;
    }
}