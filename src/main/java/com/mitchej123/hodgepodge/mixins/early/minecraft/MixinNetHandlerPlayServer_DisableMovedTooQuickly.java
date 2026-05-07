package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.config.FixesConfig;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_DisableMovedTooQuickly {

    /**
     * The vanilla "moved too quickly" check compares the squared distance of a player's movement per tick
     * against 100.0 (i.e. 10 blocks/tick). When the config option is enabled, replace this threshold with
     * Double.MAX_VALUE so the check can never trigger, suppressing the log spam and the position rollback
     * that happens with fast-moving items like GraviChestplate, MPS jetpacks, etc.
     */
    @ModifyConstant(
            method = "processPlayer",
            constant = @Constant(doubleValue = 100.0D, ordinal = 0))
    private double hodgepodge$disableMovedTooQuicklyCheck(double original) {
        return FixesConfig.disableMovedTooQuicklyCheck ? Double.MAX_VALUE : original;
    }
}
