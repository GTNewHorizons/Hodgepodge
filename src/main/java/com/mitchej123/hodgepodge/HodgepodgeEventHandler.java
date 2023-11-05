package com.mitchej123.hodgepodge;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.ZombieEvent;

import com.mitchej123.hodgepodge.net.MessageConfigSync;
import com.mitchej123.hodgepodge.net.NetworkHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

public class HodgepodgeEventHandler {

    public void preinit() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
    }

    private boolean xuDisableAidTrigger;

    @SubscribeEvent
    public void onZombieAidSummon(ZombieEvent.SummonAidEvent event) {
        if (!event.world.isRemote && xuDisableAidTrigger) {
            event.setResult(Event.Result.DENY);
        }
    }

    public void setAidTriggerDisabled(boolean disableAidTrigger) {
        xuDisableAidTrigger = disableAidTrigger;
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        NetworkHandler.instance.sendTo(new MessageConfigSync(Common.config), (EntityPlayerMP) event.player);
    }
}
