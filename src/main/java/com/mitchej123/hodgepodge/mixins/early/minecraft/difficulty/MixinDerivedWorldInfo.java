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
    public EnumDifficulty getDifficulty() {
        return ((IWorldDifficulty) this.theWorldInfo).getDifficulty();
    }

    @Override
    public void setDifficulty(EnumDifficulty difficulty) {
        // no-op — the overworld's WorldInfo is the authority
    }

    @Override
    public boolean isDifficultyLocked() {
        return ((IWorldDifficulty) this.theWorldInfo).isDifficultyLocked();
    }

    @Override
    public void setDifficultyLocked(boolean locked) {
        // no-op — lock state is owned by the overworld
    }
}
