package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.util.ArrayList;

import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.hooks.ThaumcraftMixinMethods;

import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.Marker;

@Mixin(EntityGolemBase.class)
public class MixinEntityGolemBase extends EntityGolem {

    @Shadow(remap = false)
    protected ArrayList<Marker> markers;

    private MixinEntityGolemBase(World world) {
        super(world);
    }

    @Inject(method = "readEntityFromNBT", at = @At(value = "TAIL"))
    public void hodgepodge$readEntityFromNBT(NBTTagCompound nbt, CallbackInfo ci) {
        NBTTagList nbtTagList = nbt.getTagList("markers", 10);
        ThaumcraftMixinMethods.overwriteMarkersDimID(nbtTagList, this.markers);
    }

    @ModifyArg(
            method = "readEntityFromNBT",
            at = @At(value = "INVOKE", target = "Lthaumcraft/common/entities/golems/Marker;<init>(IIIIBB)V"),
            index = 3)
    private int hodgepodge$useFullDim(int castDim, @Local(name = "dim") int dim) {
        return dim;
    }
}
