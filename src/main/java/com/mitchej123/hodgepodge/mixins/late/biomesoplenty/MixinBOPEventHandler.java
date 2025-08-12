package com.mitchej123.hodgepodge.mixins.late.biomesoplenty;

import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import biomesoplenty.client.fog.FogHandler;
import biomesoplenty.common.eventhandler.BOPEventHandlers;

@Mixin(BOPEventHandlers.class)
public class MixinBOPEventHandler {

    /**
     * @author Alexdoru
     * @reason Prevents registering an event handler that triggers an HTTP request blocking the main thread during game
     *         startup
     */
    @Overwrite(remap = false)
    private static void registerClientEventHandlers() {
        MinecraftForge.EVENT_BUS.register(new FogHandler());
        // deleted line compared to the original
        // FMLCommonHandler.instance().bus().register(new FlowerScatterEventHandler());
    }
}
