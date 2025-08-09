package com.mitchej123.hodgepodge;

import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.ZombieEvent;

import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.net.MessageConfigSync;
import com.mitchej123.hodgepodge.net.NetworkHandler;
import com.mitchej123.hodgepodge.util.TravellersGear;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;

public class HodgepodgeEventHandler {

    public static final Set<EntityPlayerMP> playersClosedContainers = new ReferenceOpenHashSet<>();

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
        NetworkHandler.instance.sendTo(new MessageConfigSync(), (EntityPlayerMP) event.player);
        if (FixesConfig.returnTravellersGearItems && !Compat.isTravellersGearPresent()) {
            TravellersGear.returnTGItems(event.player);
        }
    }

    @SubscribeEvent
    public void onItemToss(ItemTossEvent event) {
        // Already handled
        if (event.isCanceled()) return;

        if (TweaksConfig.avoidDroppingItemsWhenClosing && event.player instanceof EntityPlayerMP
                && playersClosedContainers.contains(event.player)) {
            if (event.player.inventory.addItemStackToInventory(event.entityItem.getEntityItem())) {
                event.player.inventory.setItemStack(null); // empty the item in hand
                ((EntityPlayerMP) event.player).sendContainerToPlayer(event.player.inventoryContainer);
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && TweaksConfig.avoidDroppingItemsWhenClosing
                && !playersClosedContainers.isEmpty()) {
            playersClosedContainers.clear();
        }
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (FixesConfig.fixDimensionChangeAttributes && event.player instanceof EntityPlayerMP player) {
            // fixes xp when changing dimensions
            player.addExperienceLevel(0);
        }
    }

}
