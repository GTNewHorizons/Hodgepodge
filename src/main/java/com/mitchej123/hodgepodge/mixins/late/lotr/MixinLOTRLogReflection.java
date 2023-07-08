package com.mitchej123.hodgepodge.mixins.late.lotr;

import net.minecraft.server.MinecraftServer;

import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import lotr.common.util.LOTRLog;

@Mixin(LOTRLog.class)
public class MixinLOTRLogReflection {

    @Shadow(remap = false)
    public static Logger logger;

    /**
     * @author Mist475
     * @reason Replace call to unlock final field with simple reflection
     */
    @Overwrite(remap = false)
    public static void findLogger() {
        try {
            logger = ObfuscationReflectionHelper
                    .getPrivateValue(MinecraftServer.class, null, "field_147145_h", "logger");

        } catch (final Exception e) {
            FMLLog.warning("LOTR: Failed to find logger!");
            e.printStackTrace();
        }
    }
}
