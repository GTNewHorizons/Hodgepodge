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
    private static Class<?> hp$classEE;

    /**
     * @author Pilzinsel64
     * @reason fix cascading ClassNotFoundException + Cache reflection
     */
    @Overwrite(remap = false)
    public static boolean isEETransmutionItem(Item item) {
        if (!hp$initEECheck) {
            hp$initEECheck = true;
            if (Loader.isModLoaded("ee")) {
                try {
                    hp$classEE = Class.forName("com.pahimar.ee3.item.ITransmutationStone");
                } catch (Exception ignored) {}
            }
        }
        return hp$classEE != null && hp$classEE.isAssignableFrom(item.getClass());
    }
}
