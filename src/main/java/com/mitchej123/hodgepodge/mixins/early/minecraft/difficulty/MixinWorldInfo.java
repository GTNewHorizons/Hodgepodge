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
    private EnumDifficulty hodgepodge$difficulty;

    @Unique
    private boolean hodgepodge$difficultyLocked;

    @Override
    public EnumDifficulty hodgepodge$getDifficulty() {
        return this.hodgepodge$difficulty;
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

    @Inject(method = "<init>(Lnet/minecraft/world/WorldSettings;Ljava/lang/String;)V", at = @At("RETURN"))
    private void onNewWorld(WorldSettings settings, String name, CallbackInfo ci) {
        IWorldDifficulty source = (IWorldDifficulty) (Object) settings;
        this.hodgepodge$difficulty = settings.getHardcoreEnabled() ? EnumDifficulty.HARD
                : source.hodgepodge$getDifficulty();
        this.hodgepodge$difficultyLocked = source.hodgepodge$isDifficultyLocked();
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V", at = @At("RETURN"))
    private void onLoad(NBTTagCompound nbt, CallbackInfo ci) {
        if (nbt.hasKey("Difficulty", 99)) {
            this.hodgepodge$difficulty = EnumDifficulty.getDifficultyEnum(nbt.getByte("Difficulty"));
        }

        this.hodgepodge$difficultyLocked = nbt.getBoolean("DifficultyLocked");
    }

    @Inject(method = "<init>(Lnet/minecraft/world/storage/WorldInfo;)V", at = @At("RETURN"))
    private void onCopyConstructor(WorldInfo original, CallbackInfo ci) {
        IWorldDifficulty bridge = (IWorldDifficulty) original;
        this.hodgepodge$difficulty = bridge.hodgepodge$getDifficulty();
        this.hodgepodge$difficultyLocked = bridge.hodgepodge$isDifficultyLocked();
    }

    @Inject(method = "updateTagCompound", at = @At("TAIL"))
    private void onSave(NBTTagCompound nbt, NBTTagCompound playerNbt, CallbackInfo ci) {
        if (this.hodgepodge$difficulty != null) {
            nbt.setByte("Difficulty", (byte) this.hodgepodge$difficulty.getDifficultyId());
        }

        nbt.setBoolean("DifficultyLocked", this.hodgepodge$difficultyLocked);
    }
}
