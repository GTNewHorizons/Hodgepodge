package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.common.Loader;

@Mixin(thaumcraft.common.lib.utils.Utils.class)
public class MixinUtils {

    @Unique
    private static boolean hp$initEECheck;

    @Unique
    @SuppressWarnings("rawtypes")
    private static Class hp$classEE;

    @Overwrite(remap = false)
    @SuppressWarnings("unchecked")
    public static boolean isEETransmutionItem(Item item) {
        if (!hp$initEECheck) {
            if (Loader.isModLoaded("ee")) {
                try {
                    hp$classEE = Class.forName("com.pahimar.ee3.item.ITransmutationStone");
                } catch (Exception var3) {
                    ;
                }
            }
            hp$initEECheck = true;
        }
        if (hp$classEE != null && hp$classEE.isAssignableFrom(item.getClass())) {
            return true;
        }
        return false;
    }
}
