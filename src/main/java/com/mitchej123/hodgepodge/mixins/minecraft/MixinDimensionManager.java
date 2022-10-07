package com.mitchej123.hodgepodge.mixins.minecraft;

import com.mitchej123.hodgepodge.Common;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraftforge.common.DimensionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DimensionManager.class)
public class MixinDimensionManager {

    private static Map<Integer, Set<String>> registerLogs;
    private static Map<Integer, Set<String>> unregisterLogs;

    @Inject(method = "registerDimension", at = @At("HEAD"), remap = false)
    private static void onRegisterDimension(int id, int providerType, CallbackInfo ci) {
        if (registerLogs == null) {
            // DimensionManager#init is called before #registerLogs static instantiation
            registerLogs = new HashMap<>();
        }
        registerLogs
                .computeIfAbsent(id, key -> new HashSet<>())
                .add(Arrays.toString(Thread.currentThread().getStackTrace()));
    }

    @Inject(method = "unregisterDimension", at = @At("HEAD"), remap = false)
    private static void onUnRegisterDimension(int id, CallbackInfo ci) {
        if (unregisterLogs == null) {
            unregisterLogs = new HashMap<>();
        }
        unregisterLogs
                .computeIfAbsent(id, key -> new HashSet<>())
                .add(Arrays.toString(Thread.currentThread().getStackTrace()));
    }

    @Inject(
            method = "registerDimension",
            at =
                    @At(
                            shift = At.Shift.BEFORE,
                            value = "INVOKE",
                            target =
                                    "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"),
            remap = false)
    private static void beforeThrowOnRegisterDimension(int id, int providerType, CallbackInfo ci) {
        log(id);
    }

    @Inject(
            method = "unregisterDimension",
            at =
                    @At(
                            shift = At.Shift.BEFORE,
                            value = "INVOKE",
                            target =
                                    "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"),
            remap = false)
    private static void beforeThrowOnUnRegisterDimension(int id, CallbackInfo ci) {
        log(id);
    }

    @Inject(
            method = "getProviderType",
            at =
                    @At(
                            shift = At.Shift.BEFORE,
                            value = "INVOKE",
                            target =
                                    "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"),
            remap = false)
    private static void beforeThrowOnGetProviderType(int dim, CallbackInfoReturnable<Integer> cir) {
        log(dim);
    }

    private static void log(int id) {
        Common.log.warn("DimensionManager crashed!");
        Common.log.warn("dimension id: " + id);
        Common.log.warn("logs for `registerDimension`:");
        if (registerLogs != null && registerLogs.containsKey(id)) {
            Common.log.warn(registerLogs.get(id));
        } else {
            Common.log.warn("not available");
        }
        Common.log.warn("logs for `unregisterDimension`:");
        if (unregisterLogs != null && unregisterLogs.containsKey(id)) {
            Common.log.warn(unregisterLogs.get(id));
        } else {
            Common.log.warn("not available");
        }
    }
}
