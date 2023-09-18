package com.mitchej123.hodgepodge.mixin.mixins.late.thaumcraft;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.mixin.hooks.ThaumcraftHook;

import thaumcraft.common.entities.golems.ItemGolemBell;
import thaumcraft.common.entities.golems.Marker;

@Mixin(ItemGolemBell.class)
public class MixinItemGolemBell {

    @Inject(method = "getMarkers", at = @At(value = "TAIL"), remap = false, cancellable = true)
    private static void hodgepodge$getMarkers(ItemStack stack, CallbackInfoReturnable<ArrayList<Marker>> cir) {
        ArrayList<Marker> markers = cir.getReturnValue();
        if (stack.hasTagCompound()) {
            NBTTagList nbtTagList = stack.stackTagCompound.getTagList("markers", 10);
            cir.setReturnValue(ThaumcraftHook.overwriteMarkersDimID(nbtTagList, markers));
        }
    }

}
