package com.mitchej123.hodgepodge.mixins.early.minecraft.difficulty;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.WorldInfo;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;

@Mixin(WorldInfo.class)
public abstract class MixinWorldInfo implements IWorldDifficulty {

    @Unique
    private EnumDifficulty difficulty;

    @Unique
    private boolean difficultyLocked;

    @Override
    public EnumDifficulty getDifficulty() {
        return this.difficulty;
    }

    @Override
    public void setDifficulty(EnumDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public boolean isDifficultyLocked() {
        return this.difficultyLocked;
    }

    @Override
    public void setDifficultyLocked(boolean locked) {
        this.difficultyLocked = locked;
    }

    // when creating a new world always default to normal
    @Inject(method = "<init>(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onNewWorld(WorldSettings settings, String name, CallbackInfo ci) {
        this.difficulty = EnumDifficulty.NORMAL;
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"))
    private void onLoad(NBTTagCompound nbt, CallbackInfo ci) {
        if (nbt.hasKey("Difficulty", 99)) {
            this.difficulty = EnumDifficulty.getDifficultyEnum(nbt.getByte("Difficulty"));
        }

        if (nbt.hasKey("DifficultyLocked", 1)) {
            this.difficultyLocked = nbt.getBoolean("DifficultyLocked");
        }
    }

    @Inject(method = "<init>(Lnet/minecraft/world/storage/WorldInfo;)V", at = @At("RETURN"))
    private void onCopyConstructor(WorldInfo original, CallbackInfo ci) {
        IWorldDifficulty bridge = (IWorldDifficulty) original;
        this.setDifficulty(bridge.getDifficulty());
        this.setDifficultyLocked(bridge.isDifficultyLocked());
    }

    @Inject(method = "updateTagCompound", at = @At("TAIL"))
    private void onSave(NBTTagCompound nbt, NBTTagCompound playerNbt, CallbackInfo ci) {
        if (this.difficulty != null) {
            nbt.setByte("Difficulty", (byte) this.difficulty.getDifficultyId());
        }

        nbt.setBoolean("DifficultyLocked", this.difficultyLocked);
    }

}
