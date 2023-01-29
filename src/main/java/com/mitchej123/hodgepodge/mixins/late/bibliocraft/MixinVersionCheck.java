package com.mitchej123.hodgepodge.mixins.late.bibliocraft;

import jds.bibliocraft.VersionCheck;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mixin(value = VersionCheck.class, remap = false)
public class MixinVersionCheck {

    /**
     * @author Alexdoru
     * @reason yeet
     */
    @Overwrite
    @SubscribeEvent
    public void onWorldLoad(EntityJoinWorldEvent event) {
        FMLCommonHandler.instance().bus().unregister(this);
    }

    /**
     * @author Alexdoru
     * @reason yeet
     */
    @Overwrite
    public static void getNetVersion(EntityPlayer player) {}

    /**
     * @author Alexdoru
     * @reason yeet
     */
    @Overwrite
    public static void setUpdateMessage(EntityPlayer player) {}
}
