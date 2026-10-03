package com.mitchej123.hodgepodge.mixins.interfaces;

import net.minecraft.world.EnumDifficulty;

public interface IWorldDifficulty {

    EnumDifficulty hodgepodge$getDifficulty();

    void hodgepodge$setDifficulty(EnumDifficulty difficulty);

    boolean hodgepodge$isDifficultyLocked();

    void hodgepodge$setDifficultyLocked(boolean locked);
}
