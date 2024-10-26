package com.mitchej123.hodgepodge.mixins.early.memory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(value = FMLClientHandler.class, remap = false)
public class MixinFMLClientHandler {

    @Shadow
    private SetMultimap<String, ResourceLocation> missingTextures;
    @Shadow
    private Set<String> badTextureDomains;
    @Shadow
    private Table<String, String, Set<ResourceLocation>> brokenTextures;

    @Inject(method = "logMissingTextureErrors", at = @At("TAIL"))
    private void hodgepodge$freeMemory(CallbackInfo ci) {
        missingTextures = HashMultimap.create();
        badTextureDomains = Sets.newHashSet();
        brokenTextures = HashBasedTable.create();
    }

}
