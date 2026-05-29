package com.mitchej123.hodgepodge.mixins.preinit;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mitchej123.hodgepodge.mixins.hooks.BlockLookupHooks;

import cpw.mods.fml.common.registry.GameData;

@Mixin(value = GameData.class, remap = false)
public class MixinGameData_BlockArraySync {

    @Inject(method = "freezeData", at = @At("RETURN"))
    private static void hodgepodge$rebuildBlockArrayOnFreeze(CallbackInfo ci) {
        // Once Forge has frozen the registries, rebuild the fast lookup cache
        // from the final active block registry.
        BlockLookupHooks.rebuild();
    }

    @Inject(
            method = "injectWorldIDMap(Ljava/util/Map;Ljava/util/Set;Ljava/util/Map;Ljava/util/Map;Ljava/util/Set;Ljava/util/Set;ZZ)Ljava/util/List;",
            at = @At("HEAD"))
    private static void hodgepodge$invalidateBlockArrayBeforeInject(Map<String, Integer> dataList,
            Set<Integer> blockedIds, Map<String, String> blockAliases, Map<String, String> itemAliases,
            Set<String> blockSubstitutions, Set<String> itemSubstitutions, boolean injectFrozenData,
            boolean isLocalWorld, CallbackInfoReturnable<List<String>> cir) {

        // Old-world loading may remap numeric block IDs.
        // Disable the fast cache before Forge starts injecting the world ID map
        // so stale embedded IDs cannot be returned during the remap.
        BlockLookupHooks.invalidate();
    }

    @Inject(
            method = "injectWorldIDMap(Ljava/util/Map;Ljava/util/Set;Ljava/util/Map;Ljava/util/Map;Ljava/util/Set;Ljava/util/Set;ZZ)Ljava/util/List;",
            at = @At("RETURN"))
    private static void hodgepodge$rebuildBlockArrayOnInject(Map<String, Integer> dataList, Set<Integer> blockedIds,
            Map<String, String> blockAliases, Map<String, String> itemAliases, Set<String> blockSubstitutions,
            Set<String> itemSubstitutions, boolean injectFrozenData, boolean isLocalWorld,
            CallbackInfoReturnable<List<String>> cir) {

        // The active registry is stable again after the world ID map has been injected.
        // Rebuild the fast lookup cache from that remapped registry.
        BlockLookupHooks.rebuild();
    }
}
