package com.mitchej123.hodgepodge.mixins.preinit.embedid;

import static cpw.mods.fml.common.registry.GameData.getBlockRegistry;
import static cpw.mods.fml.common.registry.GameData.getItemRegistry;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.EmbedToggle;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = GameData.class, remap = false)
public abstract class MixinGameData {

    @Shadow @Final private FMLControlledNamespacedRegistry<Block> iBlockRegistry;

    @Shadow @Final private FMLControlledNamespacedRegistry<Item> iItemRegistry;

    @Unique
    private EmbedToggle getRegistry(boolean isBlock) {
        return (EmbedToggle) (isBlock ? this.iBlockRegistry : this.iItemRegistry);
    }

    @ModifyVariable(
            method = "injectWorldIDMap(Ljava/util/Map;Ljava/util/Set;Ljava/util/Map;Ljava/util/Map;Ljava/util/Set;Ljava/util/Set;ZZ)Ljava/util/List;",
            at = @At(value = "STORE"))
    private static GameData hodgepodge$disableIDEmbed(GameData newData) {
        ((EmbedToggle) getBlockRegistry()).hodgepodge$setUseEmbed(false);
        ((EmbedToggle) getItemRegistry()).hodgepodge$setUseEmbed(false);

        ((MixinGameData) (Object) newData).getRegistry(true).hodgepodge$setUseEmbed(false);
        ((MixinGameData) (Object) newData).getRegistry(false).hodgepodge$setUseEmbed(false);
        return newData;
    }

    @Inject(
            method = "injectWorldIDMap(Ljava/util/Map;Ljava/util/Set;Ljava/util/Map;Ljava/util/Map;Ljava/util/Set;Ljava/util/Set;ZZ)Ljava/util/List;",
            at = @At(value = "RETURN"))
    private static void hodgepodge$enableIDEmbed(Map<String, Integer> dataList, Set<Integer> blockedIds, Map<String, String> blockAliases, Map<String, String> itemAliases, Set<String> blockSubstitutions, Set<String> itemSubstitutions, boolean injectFrozenData, boolean isLocalWorld, CallbackInfoReturnable<List<String>> cir, @Local(name = "newData") GameData newData) {
        ((EmbedToggle) getBlockRegistry()).hodgepodge$setUseEmbed(true);
        ((EmbedToggle) getItemRegistry()).hodgepodge$setUseEmbed(true);

        ((MixinGameData) (Object) newData).getRegistry(true).hodgepodge$setUseEmbed(true);
        ((MixinGameData) (Object) newData).getRegistry(false).hodgepodge$setUseEmbed(true);
    }
}
