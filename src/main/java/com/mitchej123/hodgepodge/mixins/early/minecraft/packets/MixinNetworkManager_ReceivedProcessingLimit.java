package com.mitchej123.hodgepodge.mixins.early.minecraft.packets;

import net.minecraft.network.NetworkManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalLongRef;
import com.mitchej123.hodgepodge.config.FixesConfig;

@Mixin(NetworkManager.class)
public class MixinNetworkManager_ReceivedProcessingLimit {

    @Unique
    private static final int HODGEPODGE$TIME_CHECK_INTERVAL = 50;

    @Definition(id = "i", local = @Local(type = int.class))
    @Expression("i = @(1000)")
    @ModifyExpressionValue(method = "processReceivedPackets", at = @At("MIXINEXTRAS:EXPRESSION"))
    public int modifyLoopInit(int constant, @Share("startTime") LocalLongRef startTime) {
        startTime.set(System.nanoTime());
        return 0;
    }

    @Definition(id = "i", local = @Local(type = int.class))
    @Expression("i >= 0")
    @ModifyExpressionValue(method = "processReceivedPackets", at = @At("MIXINEXTRAS:EXPRESSION"))
    public boolean redirectLoopCondition(boolean original, @Local(type = int.class) int i,
            @Share("startTime") LocalLongRef startTime) {
        if (i % HODGEPODGE$TIME_CHECK_INTERVAL != 0) return true;
        long now = System.nanoTime();
        return now - startTime.get() < FixesConfig.receivedPacketTimeLimit;
    }

    @Definition(id = "i", local = @Local(type = int.class))
    @Expression("i = @(i + -1)")
    @ModifyExpressionValue(method = "processReceivedPackets", at = @At("MIXINEXTRAS:EXPRESSION"))
    public int switchLoopCounterDirection(int original) {
        return original + 2;
    }
}
