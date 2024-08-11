package com.mitchej123.hodgepodge.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.event.FMLModIdMappingEvent.ModRemapping;
import cpw.mods.fml.common.event.FMLModIdMappingEvent.RemapTarget;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class StatHandler {

    public static final Map<Class<? extends Entity>, EntityEggInfo> ADDITIONAL_ENTITY_EGGS = new HashMap<>();
    public static String currentEntityName;

    // stat arrays mapped to the frozen ids
    private static final StatBase[] STATS_MINE = new StatBase[StatList.mineBlockStatArray.length];
    private static final StatBase[] STATS_CRAFT = new StatBase[StatList.objectCraftStats.length];
    private static final StatBase[] STATS_USE = new StatBase[StatList.objectUseStats.length];
    private static final StatBase[] STATS_BREAK = new StatBase[StatList.objectBreakStats.length];
    private static boolean initFrozenStats = true;

    public static void remap(ImmutableList<ModRemapping> remappedIds) {
        if (initFrozenStats) {
            // init stat arrays mapped to the frozen ids
            arraycopy(StatList.mineBlockStatArray, STATS_MINE);
            arraycopy(StatList.objectCraftStats, STATS_CRAFT);
            arraycopy(StatList.objectUseStats, STATS_USE);
            arraycopy(StatList.objectBreakStats, STATS_BREAK);
            initFrozenStats = false;
        }
        if (remappedIds.isEmpty()) {
            // we are reverting to frozen ids
            arraycopy(STATS_MINE, StatList.mineBlockStatArray);
            arraycopy(STATS_CRAFT, StatList.objectCraftStats);
            arraycopy(STATS_USE, StatList.objectUseStats);
            arraycopy(STATS_BREAK, StatList.objectBreakStats);
            return;
        }

        final StatBase[] statsMine = StatList.mineBlockStatArray.clone();
        final StatBase[] statsCraft = StatList.objectCraftStats.clone();
        final StatBase[] statsUse = StatList.objectUseStats.clone();
        final StatBase[] statsBreak = StatList.objectBreakStats.clone();

        // remap the stats with changed id
        for (ModRemapping remapping : remappedIds) {
            if (remapping.remapTarget == RemapTarget.BLOCK) {
                continue;
            }
            final int newId = remapping.newId;
            final int oldId = remapping.oldId;

            if (newId < 4096 && oldId < 4096) {
                statsMine[newId] = StatList.mineBlockStatArray[oldId];
            } else if (newId < 4096 ^ oldId < 4096) {
                // 0 - 4095 -> blocks
                // 4096+ -> items
                // switching domains is unexpected
                Common.log.warn("Unexpected remap: oldID={}, newId={}, tag={}", oldId, newId, remapping.tag);
            }
            statsCraft[newId] = StatList.objectCraftStats[oldId];
            statsUse[newId] = StatList.objectUseStats[oldId];
            statsBreak[newId] = StatList.objectBreakStats[oldId];
        }
        arraycopy(statsMine, StatList.mineBlockStatArray);
        arraycopy(statsCraft, StatList.objectCraftStats);
        arraycopy(statsUse, StatList.objectUseStats);
        arraycopy(statsBreak, StatList.objectBreakStats);
    }

    public static void addEntityStats() {
        if (!ADDITIONAL_ENTITY_EGGS.isEmpty()) {
            // only populate map once - we don't want duplicate stats
            return;
        }
        for (Entry<Class<? extends Entity>, String> e : EntityList.classToStringMapping.entrySet()) {
            Class<? extends Entity> clazz = e.getKey();
            if (!EntityLivingBase.class.isAssignableFrom(clazz)) {
                // only entities extending EntityLivingBase can be killed/can kill the player
                continue;
            }
            @SuppressWarnings("unchecked")
            Integer id = (Integer) EntityList.classToIDMapping.getOrDefault(clazz, 256);
            if (EntityList.entityEggs.containsKey(id)) {
                continue;
            }
            currentEntityName = e.getValue();
            ADDITIONAL_ENTITY_EGGS.put(clazz, new EntityInfo(id, currentEntityName));
        }
        currentEntityName = null;
        MinecraftForge.EVENT_BUS.register(new StatHandler());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onLivingDeathEvent(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayerMP player) {
            // the player was killed
            EntityLivingBase attackingEntity = player.func_94060_bK();
            if (attackingEntity == null) {
                return;
            }
            EntityEggInfo info = ADDITIONAL_ENTITY_EGGS.get(attackingEntity.getClass());
            if (info == null) {
                return;
            }
            player.addStat(info.field_151513_e, 1); // "killed by entity" stat
            return;
        }
        if (event.source.getEntity() instanceof EntityPlayer player) {
            // the player made a kill
            EntityEggInfo info = ADDITIONAL_ENTITY_EGGS.get(event.entityLiving.getClass());
            if (info == null) {
                return;
            }
            player.addStat(info.field_151512_d, 1); // "kill entity" stat

        }
    }

    private static <T> void arraycopy(T[] src, T[] dest) {
        System.arraycopy(src, 0, dest, 0, src.length);
    }

    public static class EntityInfo extends EntityEggInfo {

        public final String name;

        public EntityInfo(int id, String name) {
            super(id, 0, 0);
            this.name = name;
        }
    }
}
