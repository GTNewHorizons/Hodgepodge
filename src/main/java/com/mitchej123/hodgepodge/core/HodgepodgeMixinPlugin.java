package com.mitchej123.hodgepodge.core;

import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class HodgepodgeMixinPlugin implements IMixinConfigPlugin {
    private static final Logger log = LogManager.getLogger("Hodgepodge");
    public static LoadingConfig config;

    static {
        log.info("Initializing Hodgepodge");
        config = new LoadingConfig(new File(Launch.minecraftHome, "config/hodgepodge.cfg"));
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        List<String> mixins = new ArrayList<>();
        for (MixinSets set : MixinSets.values()) {
            if (set.applied()) {
                mixins.addAll(Arrays.asList(set.mixinClasses));
                log.info("Loading hodgepodge plugin '{}' with mixins: {}", set.name, set.mixinClasses);
            }
        }

        return mixins;
    }


    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    public enum MixinSets {
        FENCE_CONNECTIONS_FIX("Fence Connections Fix",
                () -> config.fixFenceConnections,
                "fixfenceconnections.block.MixinBlockFence"),
        GRASS_CHUNK_LOADING_FIX("Grass loads chunks Fix",
                () -> config.fixGrassChunkLoads,
                "fixgrasschunkloads.block.MixinBlockGrass"),
        NORTHWEST_BIAS_FIX("Northwest Bias Fix",
                () -> config.fixNorthWestBias,
                "fixnorthwestbias.entity.ai.MixinRandomPositionGenerator"),
        IC2_DIRECT_INV_ACCESS("IC2 Direct Inventory Access Fix",
                () -> config.fixIc2DirectInventoryAccess,
                "fixic2directinventoryaccess.item.MixinItemCropSeed",
                "fixic2directinventoryaccess.crop.MixinTileEntityCrop");


        private String name;
        private Supplier<Boolean> applyIf;
        private String[] mixinClasses;

        MixinSets(String name, Supplier<Boolean> applyIf, String... mixinClasses) {
            this.name = name;
            this.applyIf = applyIf;
            this.mixinClasses = mixinClasses;
        }

        public boolean applied() {
            return applyIf.get();
        }
    }
}
