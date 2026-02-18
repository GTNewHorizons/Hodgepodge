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
        BlockLookupHooks.rebuild();
    }

    @Inject(
            method = "injectWorldIDMap(Ljava/util/Map;Ljava/util/Set;Ljava/util/Map;Ljava/util/Map;Ljava/util/Set;Ljava/util/Set;ZZ)Ljava/util/List;",
            at = @At("RETURN"))
    private static void hodgepodge$rebuildBlockArrayOnInject(Map<String, Integer> dataList, Set<Integer> blockedIds,
            Map<String, String> blockAliases, Map<String, String> itemAliases, Set<String> blockSubstitutions,
            Set<String> itemSubstitutions, boolean injectFrozenData, boolean isLocalWorld,
            CallbackInfoReturnable<List<String>> cir) {
        BlockLookupHooks.rebuild();
    }
}
