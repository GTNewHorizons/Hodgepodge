package com.mitchej123.hodgepodge.core.fml.hooks.mc;

import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import com.gtnewhorizon.gtnhlib.util.ServerThreadUtil;
import com.mitchej123.hodgepodge.mixins.interfaces.IWorldDifficulty;
import com.mitchej123.hodgepodge.net.MessageServerDifficulty;
import com.mitchej123.hodgepodge.net.NetworkHandler;

/**
 * replace every use of World.difficultySetting with our hook
 */
@SuppressWarnings("unused")
public final class DifficultyHook {

    private DifficultyHook() {}

    public static EnumDifficulty getDifficulty(World world) {
        final WorldInfo info = world.getWorldInfo();
        if (!(info instanceof IWorldDifficulty worldDifficulty)) return EnumDifficulty.NORMAL;
        return worldDifficulty.getDifficulty();
    }

    public static void setDifficulty(World world, EnumDifficulty difficulty) {
        final WorldInfo info = world.getWorldInfo();
        if (!(info instanceof IWorldDifficulty worldDifficulty)) return;
        if (!worldDifficulty.isDifficultyLocked()) {
            worldDifficulty.setDifficulty(difficulty);

            // send the update to the client
            if (!world.isRemote && world.provider != null && ServerThreadUtil.isCallingFromMinecraftThread()) {
                NetworkHandler.instance
                        .sendToDimension(new MessageServerDifficulty(difficulty, false), world.provider.dimensionId);
            }
        }
    }
}
