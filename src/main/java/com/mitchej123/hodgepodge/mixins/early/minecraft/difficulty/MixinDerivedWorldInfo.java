package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.storage.DerivedWorldInfo;
import net.minecraft.world.storage.WorldInfo;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;

@Mixin(DerivedWorldInfo.class)
public abstract class MixinDerivedWorldInfo extends MixinWorldInfo {

    @Final
    @Shadow
    private WorldInfo theWorldInfo;

    @Override
    public EnumDifficulty hodgepodge$getDifficulty() {
        return ((IWorldDifficulty) this.theWorldInfo).hodgepodge$getDifficulty();
    }

    @Override
    public void hodgepodge$setDifficulty(EnumDifficulty difficulty) {}

    @Override
    public boolean hodgepodge$isDifficultyLocked() {
        return ((IWorldDifficulty) this.theWorldInfo).hodgepodge$isDifficultyLocked();
    }

    @Override
    public void hodgepodge$setDifficultyLocked(boolean locked) {}
}
