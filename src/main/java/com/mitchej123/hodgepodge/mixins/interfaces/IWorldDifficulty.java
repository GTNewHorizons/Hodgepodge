package com.mitchej123.hodgepodge.mixins.interfaces;

import net.minecraft.world.EnumDifficulty;

public interface IWorldDifficulty {

    EnumDifficulty getDifficulty();

    void setDifficulty(EnumDifficulty difficulty);

    boolean isDifficultyLocked();

    void setDifficultyLocked(boolean locked);
}
