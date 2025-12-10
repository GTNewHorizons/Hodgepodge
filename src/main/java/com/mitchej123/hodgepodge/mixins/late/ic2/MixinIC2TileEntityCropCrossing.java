package com.mitchej123.hodgepodge.mixins.late.ic2;

import java.util.List;
import java.util.Random;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;

import ic2.api.crops.CropCard;
import ic2.core.IC2;
import ic2.core.crop.TileEntityCrop;

@Mixin(TileEntityCrop.class)
public class MixinIC2TileEntityCropCrossing extends TileEntity {

    // Add 50% chance that if all the parents' stats are identical and the child is the same species as all of them,
    // then the child's stats will match the parents'. For crop propagation once the player has a seed they want to farm
    @Inject(
            method = "attemptCrossing",
            at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 1),
            cancellable = true,
            remap = false)
    public void hodgepodge$attemptCrossing(CallbackInfoReturnable<Boolean> cir, @Local List<TileEntityCrop> cropTes) {
        TileEntityCrop thisObject = (TileEntityCrop) (Object) this;

        CropCard crop = cropTes.get(0).getCrop();
        if (thisObject.getCrop() == crop) return;

        int growth = cropTes.get(0).getGrowth();
        int resistance = cropTes.get(0).getResistance();
        int gain = cropTes.get(0).getGain();

        for (int i = 1; i < cropTes.size(); i++) {
            if (cropTes.get(i).getGrowth() != growth || cropTes.get(i).getResistance() != resistance
                    || cropTes.get(i).getGain() != gain
                    || cropTes.get(i).getCrop() != crop)
                return;
        }

        if (IC2.random.nextInt(2) == 1) {
            thisObject.upgraded = false;
            thisObject.setCrop(crop);
            thisObject.dirty = true;
            thisObject.size = 1;
            thisObject.statGrowth = growth;
            thisObject.statResistance = resistance;
            thisObject.statGain = gain;
            cir.setReturnValue(true);
        }
    }

    // Give 25% chance that if growth is 24 and larger (which causes weeding behavior)
    // then it will be lowered back to 23
    @Inject(method = "attemptCrossing", at = @At(value = "TAIL"), remap = false)
    public void hodgepodge$attemptCrossing(CallbackInfoReturnable<Boolean> cir) {
        TileEntityCrop thisObject = (TileEntityCrop) (Object) this;
        if (thisObject.statGrowth >= 24) {
            if (IC2.random.nextInt(4) == 0) thisObject.statGrowth = 23;
        }
    }

    // Make it so stats can only drop by parent count / 2, instead of the parent count
    @Redirect(
            method = "attemptCrossing",
            at = @At(value = "INVOKE", target = "Ljava/util/Random;nextInt(I)I"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Ljava/util/List;size()I", ordinal = 1),
                    to = @At(value = "INVOKE", target = "Lic2/core/util/Util;limit(III)I")),
            remap = false)
    public int hodgepodge$attemptCrossing(Random instance, int i, @Local List<TileEntityCrop> cropTes) {
        // Resulting line will look like
        // this.statGrowth += IC2.random.nextInt(count * 1.5) - (count / 2);
        int count = cropTes.size();
        double x = count * 0.5F;
        int y;
        if (x % 1 != 0)
            // if there are 3 parents randomly choose between (-1 to 3) and (-2 to 3)
            y = (int) (Math.floor(x) + IC2.random.nextInt(2));
        else y = (int) x;
        return IC2.random.nextInt(count + y + 1) - y + count;
    }
}
