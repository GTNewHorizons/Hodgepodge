package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;

@Mixin(WorldSettings.class)
public abstract class MixinWorldSettings implements IWorldDifficulty {

    @Unique
    private EnumDifficulty hodgepodge$difficulty = EnumDifficulty.NORMAL;

    @Unique
    private boolean hodgepodge$difficultyLocked;

    @Override
    public EnumDifficulty hodgepodge$getDifficulty() {
        return this.hodgepodge$difficulty == null ? EnumDifficulty.NORMAL : this.hodgepodge$difficulty;
    }

    @Override
    public void hodgepodge$setDifficulty(EnumDifficulty difficulty) {
        this.hodgepodge$difficulty = difficulty;
    }

    @Override
    public boolean hodgepodge$isDifficultyLocked() {
        return this.hodgepodge$difficultyLocked;
    }

    @Override
    public void hodgepodge$setDifficultyLocked(boolean locked) {
        this.hodgepodge$difficultyLocked = locked;
    }

    @Inject(method = "<init>(Lnet/minecraft/world/storage/WorldInfo;)V", at = @At("RETURN"))
    private void copyDifficulty(WorldInfo info, CallbackInfo ci) {
        IWorldDifficulty source = (IWorldDifficulty) info;
        EnumDifficulty difficulty = source.hodgepodge$getDifficulty();
        if (difficulty != null) this.hodgepodge$difficulty = difficulty;
        this.hodgepodge$difficultyLocked = source.hodgepodge$isDifficultyLocked();
    }
}
