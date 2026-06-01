package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.TextureMapAsyncIconsHook;

@Mixin(TextureMap.class)
public class MixinTextureMap_AsyncIcons implements TextureMapAsyncIconsHook {

    @Unique
    private static final Logger hodgepodge$LOGGER = LogManager.getLogger("Hodgepodge/Async Icons");

    @Shadow
    @Final
    @Mutable
    private Map<?, ?> mapRegisteredSprites;

    @Unique
    private final Map<String, CompletableFuture<IIcon>> hodgepodge$processingIcons = new ConcurrentHashMap<>();

    @Final
    @Shadow
    private int textureType;

    @Shadow
    @Final
    private String basePath;

    @Inject(
            method = "<init>(ILjava/lang/String;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/Maps;newHashMap()Ljava/util/HashMap;",
                    shift = Shift.AFTER,
                    ordinal = 1,
                    remap = false))
    private void hodgepodge$changeToConcurrent(int p_i1281_1_, String p_i1281_2_, boolean skipFirst, CallbackInfo ci) {
        mapRegisteredSprites = new ConcurrentHashMap<>(33_500);
    }

    /**
     * Removes an unnecessary call to registerIcons in the constructor
     */
    @Redirect(
            method = "<init>(ILjava/lang/String;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureMap;registerIcons()V"))
    private void hodgepodge$dontRegisterIconsInInit(TextureMap instance) {}

    /**
     * @author tiffit
     * @reason Rewritten to use multiple threads to load icons
     */
    @Overwrite
    private void registerIcons() {
        this.mapRegisteredSprites.clear();
        long startTime = System.currentTimeMillis();
        int threadCount = Runtime.getRuntime().availableProcessors();

        hodgepodge$LOGGER.info("Starting async icon loading with {} threads for {}", threadCount, basePath);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount, r -> {
            Thread t = new Thread(r, "IconLoader");
            t.setDaemon(true);
            return t;
        });
        if (this.textureType == 0) {
            List<Future<?>> futures = new ArrayList<>(Block.blockRegistry.getKeys().size());
            for (Object obj : Block.blockRegistry) {
                if (obj instanceof Block block) {
                    if (block.getMaterial() != Material.air) {
                        futures.add(executor.submit(() -> block.registerBlockIcons((IIconRegister) this)));
                    }
                }
            }
            hodgepodge$LOGGER.info("Loading icons for {} blocks", futures.size());
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    hodgepodge$LOGGER.error("Error loading block icon", e);
                }
            }
            Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons((IIconRegister) this);
            RenderManager.instance.updateIcons((IIconRegister) this);
            hodgepodge$LOGGER.info("Block icons loaded!");
        }

        List<Future<?>> futures = new ArrayList<>(Item.itemRegistry.getKeys().size());
        for (Object obj : Item.itemRegistry) {
            if (obj instanceof Item item) {
                if (item.getSpriteNumber() == this.textureType) {
                    futures.add(executor.submit(() -> item.registerIcons((IIconRegister) this)));
                }
            }
        }
        hodgepodge$LOGGER.info("Loading icons for {} item", futures.size());
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                hodgepodge$LOGGER.error("Error loading item icon", e);
            }
        }
        hodgepodge$LOGGER.info("Item icons loaded!");
        hodgepodge$processingIcons.clear();
        executor.shutdown();
        long time = System.currentTimeMillis() - startTime;
        hodgepodge$LOGGER.info("Finished async icon loading in {}ms", time);

    }

    /**
     * Fixes a race-condition where the same texture will be created multiple times. This checks if an icon for the
     * texture is being created already and if so, wait for that to finish and use that.
     */
    @Redirect(
            method = "registerIcon",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object hodgepodge$checkProcessing(Map<?, ?> instance, Object o) {
        Object val = instance.get(o);
        if (val != null) return val;
        boolean[] newlyCreated = { false };
        CompletableFuture<IIcon> future = hodgepodge$processingIcons.computeIfAbsent((String) o, s -> {
            newlyCreated[0] = true;
            return new CompletableFuture<>();
        });
        if (!newlyCreated[0]) {
            try {
                return future.get();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Inject(
            method = "registerIcon",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                    shift = Shift.AFTER))
    private void hodgepodge$finishProcessing(String key, CallbackInfoReturnable<IIcon> cir, @Local Object object) {
        hodgepodge$processingIcons.remove(key).complete((IIcon) object);
    }

    @Override
    public Map<String, CompletableFuture<IIcon>> hodgepodge$getProcessingIcons() {
        return hodgepodge$processingIcons;
    }
}
