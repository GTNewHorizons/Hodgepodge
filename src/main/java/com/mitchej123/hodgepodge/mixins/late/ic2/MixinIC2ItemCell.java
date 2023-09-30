package com.mitchej123.hodgepodge.mixins.late.ic2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ic2.core.Ic2Items;
import ic2.core.init.InternalName;
import ic2.core.item.ItemIC2;
import ic2.core.item.resources.ItemCell;

@Mixin(value = ItemCell.class, remap = false)
abstract public class MixinIC2ItemCell extends ItemIC2 {

    public MixinIC2ItemCell(InternalName internalName) {
        super(internalName);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void hodgepodge$init(CallbackInfo ci) {
        setContainerItem(Ic2Items.cell.getItem());
    }
}
