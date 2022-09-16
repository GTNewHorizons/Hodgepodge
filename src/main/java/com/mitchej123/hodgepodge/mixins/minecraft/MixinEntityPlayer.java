package com.mitchej123.hodgepodge.mixins.minecraft;

import com.mitchej123.hodgepodge.Hodgepodge;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {

    @Redirect(
            method = "onLivingUpdate",
            at = @At(value = "INVOKE", target = "Ljava/util/List;size()I", remap = false),
            slice =
                    @Slice(
                            from =
                                    @At(
                                            value = "INVOKE",
                                            target =
                                                    "Lnet/minecraft/world/World;getEntitiesWithinAABBExcludingEntity(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;")))
    public int hodgepodge$ThrottleItemPickupEvent(List instance) {
        return Math.min(Hodgepodge.config.itemStacksPickedUpPerTick, instance.size());
    }
}
