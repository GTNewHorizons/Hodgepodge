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
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.mixins.interfaces.TextureMapAsyncIconsHook;

@Mixin(value = TextureMap.class, priority = 1100)
public class MixinTextureMap_AsyncIcons implements TextureMapAsyncIconsHook {

    @Unique
    private static final Logger hodgepodge$LOGGER = LogManager.getLogger("Hodgepodge/Async Icons");

    @Shadow
    @Final
    @Mutable
    private Map<?, ?> mapRegisteredSprites = new ConcurrentHashMap<>();

    @Unique
    private final Map<String, CompletableFuture<IIcon>> hodgepodge$processingIcons = new ConcurrentHashMap<>();

    @Shadow
    @Final
    private String basePath;

    @Unique
    private ExecutorService hodgepodge$executor;

    @Unique
    private List<Future<?>> hodgepodge$pending;

    @Unique
    private long hodgepodge$startTime;

    /**
     * @author tiffit
     * @reason Rewritten to use multiple threads to load icons
     */
    @Inject(method = "registerIcons", at = @At("HEAD"))
    private void hodgepodge$startAsyncIconLoading(CallbackInfo ci) {
        final int threadCount = Runtime.getRuntime().availableProcessors();
        hodgepodge$startTime = System.currentTimeMillis();
        hodgepodge$pending = new ArrayList<>();
        hodgepodge$LOGGER.info("Starting async icon loading with {} threads for {}", threadCount, basePath);
        hodgepodge$executor = Executors.newFixedThreadPool(threadCount, r -> {
            Thread t = new Thread(r, "IconLoader");
            t.setDaemon(true);
            return t;
        });
    }

    @WrapOperation(
            method = "registerIcons",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/Block;registerBlockIcons(Lnet/minecraft/client/renderer/texture/IIconRegister;)V"))
    private void hodgepodge$registerBlockIconsAsync(Block block, IIconRegister register, Operation<Void> original) {
        hodgepodge$pending.add(hodgepodge$executor.submit(() -> { original.call(block, register); }));
    }

    /**
     * Destroy block icons and the render manager register on the calling thread, so every block must be done first.
     */
    @Inject(
            method = "registerIcons",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/RenderGlobal;registerDestroyBlockIcons(Lnet/minecraft/client/renderer/texture/IIconRegister;)V"))
    private void hodgepodge$awaitBlockIcons(CallbackInfo ci) {
        hodgepodge$await("block");
        hodgepodge$LOGGER.info("Block icons loaded!");
    }

    @WrapOperation(
            method = "registerIcons",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/Item;registerIcons(Lnet/minecraft/client/renderer/texture/IIconRegister;)V"))
    private void hodgepodge$registerItemIconsAsync(Item item, IIconRegister register, Operation<Void> original) {
        hodgepodge$pending.add(hodgepodge$executor.submit(() -> { original.call(item, register); }));
    }

    @Inject(method = "registerIcons", at = @At("RETURN"))
    private void hodgepodge$finishAsyncIconLoading(CallbackInfo ci) {
        hodgepodge$await("item");
        hodgepodge$LOGGER.info("Item icons loaded!");
        hodgepodge$processingIcons.clear();
        hodgepodge$executor.shutdown();
        hodgepodge$executor = null;
        hodgepodge$pending = null;
        hodgepodge$LOGGER
                .info("Finished async icon loading in {}ms", System.currentTimeMillis() - hodgepodge$startTime);
    }

    @Unique
    private void hodgepodge$await(String kind) {
        hodgepodge$LOGGER.info("Loading icons for {} {}s", hodgepodge$pending.size(), kind);
        for (Future<?> future : hodgepodge$pending) {
            try {
                future.get();
            } catch (Exception e) {
                hodgepodge$LOGGER.error("Error loading {} icon", kind, e);
            }
        }
        hodgepodge$pending.clear();
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
