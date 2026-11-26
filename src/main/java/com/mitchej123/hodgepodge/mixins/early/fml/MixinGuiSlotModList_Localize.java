package com.mitchej123.hodgepodge.mixins.early.fml;

import net.minecraft.client.resources.I18n;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import cpw.mods.fml.client.GuiSlotModList;
import cpw.mods.fml.common.ModMetadata;

@Mixin(value = GuiSlotModList.class, remap = false)
public class MixinGuiSlotModList_Localize {

    @ModifyConstant(method = "drawSlot", constant = @Constant(stringValue = "DISABLED"))
    private String hodgepodge$localizeDisabled(String original) {
        return I18n.format("hodgepodge.modlist.disabled");
    }

    @ModifyConstant(method = "drawSlot", constant = @Constant(stringValue = "Metadata not found"))
    private String hodgepodge$localizeMissingMetadata(String original) {
        return I18n.format("hodgepodge.modlist.no_metadata");
    }

    @WrapOperation(
            method = "drawSlot",
            at = @At(
                    value = "INVOKE",
                    target = "Lcpw/mods/fml/common/ModMetadata;getChildModCountString()Ljava/lang/String;",
                    remap = false))
    private String hodgepodge$localizeChildCount(ModMetadata metadata, Operation<String> original) {
        int count = metadata.childMods.size();
        return I18n.format(count == 1 ? "hodgepodge.modlist.child_count.one" : "hodgepodge.modlist.child_count", count);
    }
}
