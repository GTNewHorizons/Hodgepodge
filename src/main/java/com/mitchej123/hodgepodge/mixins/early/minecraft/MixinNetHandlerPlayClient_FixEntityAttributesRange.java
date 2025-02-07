package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.network.NetHandlerPlayClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NetHandlerPlayClient.class)
public class MixinNetHandlerPlayClient_FixEntityAttributesRange {

    /**
     * @author raylras
     * @reason Prevent the client from crashing due to invalid entity attributes range.
     *
     * @see <a href="https://bugs.mojang.com/browse/MC-150405">MC-150405</a>
     * @see <a href=
     *      "https://github.com/MinecraftForge/MinecraftForge/commit/05f4f5ec775fbba7cf5c0421041f417dd4c70a36">MinecraftForge
     *      Patch</a>
     */
    @ModifyConstant(method = "handleEntityProperties", constant = @Constant(doubleValue = Double.MIN_NORMAL))
    private static double hodgepodge$FixEntityAttributesRange(double original) {
        return -Double.MAX_VALUE;
    }
}
