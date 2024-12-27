package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.scoreboard.ScoreboardSaveData;

import org.spongepowered.asm.mixin.Mixin;

import com.mitchej123.hodgepodge.mixins.interfaces.SafeWriteNBT;

@Mixin(ScoreboardSaveData.class)
public class MixinScoreboardSaveData_threadedIO implements SafeWriteNBT {
    // Added interface
}
