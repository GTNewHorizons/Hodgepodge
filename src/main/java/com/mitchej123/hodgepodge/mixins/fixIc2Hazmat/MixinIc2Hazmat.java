package com.mitchej123.hodgepodge.mixins.fixIc2Hazmat;

import ic2.core.item.armor.ItemArmorHazmat;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import gregtech.api.util.GT_Utility;

@Mixin(value = ItemArmorHazmat.class, remap = false)
public class MixinIc2Hazmat {
    @Overwrite()
    public static boolean hasCompleteHazmat(EntityLivingBase entity) {
        if(GT_Utility.isWearingFullRadioHazmat(entity)) {
            return true;
        }
        return false;
    }
}
