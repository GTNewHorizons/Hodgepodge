package com.mitchej123.hodgepodge.core;

import net.minecraft.launchwrapper.Launch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.lib.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import ru.timeconqueror.spongemixins.MinecraftURLClassPath;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class HodgepodgeMixinPlugin implements IMixinConfigPlugin {
    private static final Logger log = LogManager.getLogger("Hodgepodge");
    public static LoadingConfig config;
    public static boolean thermosTainted;

    static {
        log.info("Initializing Hodgepodge");
        config = new LoadingConfig(new File(Launch.minecraftHome, "config/hodgepodge.cfg"));
        log.info("Looking for Thermos/Bukkit taint.");
        try {
            Class.forName("org.bukkit.World");
            thermosTainted = true;
            log.warn("Thermos/Bukkit detected; This is an unsupported configuration -- Things may not function properly.");
        } catch (ClassNotFoundException e) {
            thermosTainted = false;
            log.info("Thermos/Bukkit NOT detected :-D");
        }

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
        for (MixinSets mixin : MixinSets.values()) {
            if (mixin.shouldBeLoaded() && mixin.loadJar()) {
                mixins.addAll(mixin.mixinClasses);
                log.info("Loading hodgepodge plugin '{}' with mixins: {}", mixin.name, mixin.mixinClasses);
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
                Collections.singletonList("fixfenceconnections.block.MixinBlockFence")
        ),
        CHUNK_COORDINATES_HASHCODE("Speedup ChunkCoordinates Hashcode",
                () -> config.speedupChunkCoordinatesHashCode,
               Collections.singletonList("speedupChunkCoordinatesHashCode.MixinChunkCoordinates")
        ),
        VANILLA_UNPROTECTED_GET_BLOCK_FIX("Various vanilla unprotected getBlock fixes ",
                () -> config.fixVanillaUnprotectedGetBlock,
                Arrays.asList(
                    "fixUnprotectedGetBlock.vanilla.MixinBlockGrass",
                    "fixUnprotectedGetBlock.vanilla.MixinBlockFire",
                    "fixUnprotectedGetBlock.vanilla.MixinVillageCollection",
                    "fixUnprotectedGetBlock.vanilla.MixinBlockFluidClassic"
                )
        ),
        IC2_UNPROTECTED_GET_BLOCK_FIX("IC2 unprotected getBlock fixes",
               () -> config.fixIc2UnprotectedGetBlock,
               Collections.singletonList(
                  "fixUnprotectedGetBlock.ic2.MixinIc2WaterKinetic"
               )
        ),
        THAUMCRAFT_UNPROTECTED_GET_BLOCK_FIX("Thaumcraft unprotected getBlock fixes",
             () -> config.fixThaumcraftUnprotectedGetBlock,
             "Thaumcraft-1.7.10",
             Arrays.asList(
                 "fixUnprotectedGetBlock.thaumcraft.fixArcaneLampGetBlock",
                 "fixUnprotectedGetBlock.thaumcraft.fixEntityWispGetBlock"
              )
        ),
        NORTHWEST_BIAS_FIX("Northwest Bias Fix",
                () -> config.fixNorthWestBias,
                Collections.singletonList("fixnorthwestbias.entity.ai.MixinRandomPositionGenerator")
        ),
        IC2_DIRECT_INV_ACCESS("IC2 Direct Inventory Access Fix",
                () -> config.fixIc2DirectInventoryAccess,
                Arrays.asList(
                    "fixic2directinventoryaccess.item.MixinItemCropSeed",
                    "fixic2directinventoryaccess.crop.MixinTileEntityCrop"
                 )
        ),
        IC2_NIGHT_VISION("IC2 Nightvision adjustment",
                 () -> config.fixIc2Nightvision,
                 Arrays.asList(
                     "fixIc2Nightvision.MixinIc2NanoSuitNightVision",
                     "fixIc2Nightvision.MixinIc2QuantumSuitNightVision"
                  )
         ),
        HUNGER_OVERHAUL_FIX("Hunger Overhaul Fix",
                () -> config.fixHungerOverhaul,
                "HungerOverhaul",
                Collections.singletonList("fixHungerOverhaul.MixinHungerOverhaulLowStatEffect")
        ),
        REMOVE_COFH_CORE_UPDATE_CHECKS("Remove outdated COFH Core update check",
                 () -> config.removeUpdateChecks,
                 Collections.singletonList("removeUpdateChecks.CoFHCore.MixinCoFHCoreUpdateCheck")
        );


        private final String name;
        private final Supplier<Boolean> applyIf;
        private final List<String> mixinClasses;
        private final String jarName;

        MixinSets(String name, Supplier<Boolean> applyIf, List<String> mixinClasses) {
            this.name = name;
            this.applyIf = applyIf;
            this.mixinClasses = mixinClasses;
            this.jarName = null;
        }        
        
        MixinSets(String name, Supplier<Boolean> applyIf, String jarName, List<String> mixinClasses) {
            this.name = name;
            this.applyIf = applyIf;
            this.mixinClasses = mixinClasses;
            this.jarName = jarName; 
        }

        public boolean shouldBeLoaded() {
            return applyIf.get();
        }
        
        public boolean loadJar() {
            try {
                if( jarName == null) return true;
                File jar = MinecraftURLClassPath.getJarInModPath(jarName);
                if(jar == null) {
                    log.info("Jar not found: " + jarName);
                    return false;
                }
                
                log.info("Attempting to add " + jar.toString() + " to the URL Class Path");
                if(!jar.exists())
                    throw new FileNotFoundException(jar.toString());
                MinecraftURLClassPath.addJar(jar);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
