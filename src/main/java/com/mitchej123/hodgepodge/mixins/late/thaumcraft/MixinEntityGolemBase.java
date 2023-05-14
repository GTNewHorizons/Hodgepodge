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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.common.ThaumcraftMixinMethods;

import thaumcraft.common.entities.golems.EntityGolemBase;
import thaumcraft.common.entities.golems.Marker;

@Mixin(EntityGolemBase.class)
public class MixinEntityGolemBase extends EntityGolem {

    @Shadow
    protected ArrayList<Marker> markers;

    public MixinEntityGolemBase(World p_i1686_1_) {
        super(p_i1686_1_);
    }

    @Inject(method = "readEntityFromNBT", at = @At(value = "TAIL"))
    public void hodgepodge$readEntityFromNBT(NBTTagCompound nbt, CallbackInfo ci) {
        NBTTagList nbtTagList = nbt.getTagList("markers", 10);
        ThaumcraftMixinMethods.overwriteMarkersDimID(nbtTagList, this.markers);
    }

}
