package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.resources.AbstractResourcePack;
import net.minecraft.util.ResourceLocation;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AbstractResourcePack.class)
public class MixinAbstractResourcePack {

    /**
     * @author danyadev
     * @reason Avoid String.format allocation overhead on every resource pack lookup
     */
    @Overwrite
    private static String locationToName(ResourceLocation location) {
        return "assets/" + location.getResourceDomain() + "/" + location.getResourcePath();
    }
}
