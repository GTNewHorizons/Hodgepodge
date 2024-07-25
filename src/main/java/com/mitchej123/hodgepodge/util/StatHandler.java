package com.mitchej123.hodgepodge.util;

import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.event.FMLModIdMappingEvent.ModRemapping;
import cpw.mods.fml.common.event.FMLModIdMappingEvent.RemapTarget;

public class StatHandler {

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
            initFrozenStats = true;
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
            statsMine[newId] = StatList.mineBlockStatArray[oldId];
            statsCraft[newId] = StatList.objectCraftStats[oldId];
            statsUse[newId] = StatList.objectUseStats[oldId];
            statsBreak[newId] = StatList.objectBreakStats[oldId];
        }
        arraycopy(statsMine, StatList.mineBlockStatArray);
        arraycopy(statsCraft, StatList.objectCraftStats);
        arraycopy(statsUse, StatList.objectUseStats);
        arraycopy(statsBreak, StatList.objectBreakStats);
    }

    private static <T> void arraycopy(T[] src, T[] dest) {
        System.arraycopy(src, 0, dest, 0, src.length);
    }

}
