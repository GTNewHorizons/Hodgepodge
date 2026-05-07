package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import com.mitchej123.hodgepodge.config.FixesConfig;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer_DisableMovedTooQuickly {

    /**
     * The vanilla "moved too quickly" check compares the squared distance of a player's movement per tick against
     * 100.0 (i.e. 10 blocks/tick in a single axis). Fast-movement items like GraviChestplate combined with
     * ThaumicBoots or MPS jetpacks can legitimately exceed this threshold, causing log spam and position rollbacks.
     * <p>
     * Replaces the hardcoded 100.0 with the configured threshold. Values below 100.0 fall back to vanilla.
     */
    @ModifyConstant(method = "processPlayer", constant = @Constant(doubleValue = 100.0D, ordinal = 0))
    private double hodgepodge$movedTooQuicklyThreshold(double original) {
        double configured = FixesConfig.movedTooQuicklyThreshold;
        return configured > original ? configured : original;
    }
}
