package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import jds.bibliocraft.blocks.BlockItemLamp;
import jds.bibliocraft.blocks.BlockItemLantern;
import jds.bibliocraft.blocks.BlockItemSwordPedestal;

@Mixin({ BlockItemLamp.class, BlockItemLantern.class, BlockItemSwordPedestal.class })
public class MixinBibliocraftItem_TooltipMetaSafety {

    @Redirect(
            method = "addInformation",
            at = @At(value = "FIELD", target = "desc=/^\\[Ljava/lang/String;$/", args = "array=get"))
    private String safeIndexArray(String[] array, int index) {
        // No point to localize this, this is an error state, and the name of the item has most likely also failed to
        // localize.
        return index < array.length ? array[index] : "Invalid Color";
    }
}
