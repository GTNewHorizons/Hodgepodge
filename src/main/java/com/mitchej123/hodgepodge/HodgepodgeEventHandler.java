package com.mitchej123.hodgepodge;

import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.ZombieEvent;

import com.mitchej123.hodgepodge.config.FixesConfig;
import com.mitchej123.hodgepodge.config.SoundConfig;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.net.MessageConfigSync;
import com.mitchej123.hodgepodge.net.NetworkHandler;
import com.mitchej123.hodgepodge.util.ServerThreadLongHashMap;
import com.mitchej123.hodgepodge.util.TravellersGear;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class HodgepodgeEventHandler {

    public static final Map<EntityPlayerMP, Boolean> closingContainers = new IdentityHashMap<>();

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
                && closingContainers.containsKey(event.player)) {
            ItemStack stack = event.entityItem.getEntityItem();
            returnStack(event.player, stack);
            closingContainers.put((EntityPlayerMP) event.player, true);
            if (stack.stackSize == 0) {
                event.setCanceled(true);
            }
        }
    }

    public static void returnStack(EntityPlayer player, ItemStack stack) {
        boolean creative = player.capabilities.isCreativeMode;
        try {
            player.capabilities.isCreativeMode = false;
            player.inventory.addItemStackToInventory(stack);
        } finally {
            player.capabilities.isCreativeMode = creative;
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            ServerThreadLongHashMap.refreshSnapshots();
        }
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (FixesConfig.fixDimensionChangeAttributes && event.player instanceof EntityPlayerMP player) {
            // fixes xp when changing dimensions
            player.addExperienceLevel(0);
        }
    }

    @SubscribeEvent
    public void onConfigChange(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals("hodgepodge")) {
            SoundConfig.apply();
        }
    }

}
