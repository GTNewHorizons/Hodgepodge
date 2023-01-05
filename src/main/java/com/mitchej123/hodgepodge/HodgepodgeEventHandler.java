package com.mitchej123.hodgepodge;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.living.ZombieEvent;

public class HodgepodgeEventHandler {
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
}
