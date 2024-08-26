package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.util.StatHandler;

@Mixin(targets = "net.minecraft.client.gui.achievement.GuiStats$StatsItem")
public class MixinStatsItem {

    @Redirect(
            at = @At(
                    args = "array=get",
                    opcode = Opcodes.GETSTATIC,
                    target = "Lnet/minecraft/stats/StatList;objectBreakStats:[Lnet/minecraft/stats/StatBase;",
                    value = "FIELD"),
            expect = 2,
            method = "<init>")
    private StatBase hodgepodge$preventBreakAIOOBE(StatBase[] array, int index, @Local StatCrafting statcrafting) {
        return StatHandler.checkBounds(array, index, statcrafting);
    }

    @Redirect(
            at = @At(
                    args = "array=get",
                    opcode = Opcodes.GETSTATIC,
                    target = "Lnet/minecraft/stats/StatList;objectCraftStats:[Lnet/minecraft/stats/StatBase;",
                    value = "FIELD"),
            expect = 2,
            method = "<init>")
    private StatBase hodgepodge$preventCraftAIOOBE(StatBase[] array, int index, @Local StatCrafting statcrafting) {
        return StatHandler.checkBounds(array, index, statcrafting);
    }

}
