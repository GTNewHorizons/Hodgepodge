package com.mitchej123.hodgepodge.mixins.late.ic2;

import gregtech.api.util.GT_Utility;
import ic2.core.item.armor.ItemArmorHazmat;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = ItemArmorHazmat.class, remap = false)
public class MixinIc2Hazmat {
    /**
     * @author Sphyix
     * @reason Hazmat - IC2 logic superseded by GT check
     */
    @Overwrite()
    public static boolean hasCompleteHazmat(EntityLivingBase entity) {
        return GT_Utility.isWearingFullRadioHazmat(entity);
    }
}
