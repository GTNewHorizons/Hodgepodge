package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;
import java.util.Set;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {

    @Shadow
    private static List keybindArray;
    @Shadow
    private static IntHashMap hash;
    @Shadow
    private static Set keybindSet;
    @Shadow
    private int pressTime;
    @Shadow
    private boolean pressed;

    @Unique
    private static Multimap<Integer, KeyBinding> keybindMatcher = ArrayListMultimap.create();

    /**
     * @author eigenraven
     * @reason Needed to replace a hashmap lookup with multimap iteration
     */
    @Overwrite
    public static void onTick(int keyCode) {
        if (keyCode != 0) {
            for (KeyBinding bind : keybindMatcher.get(keyCode)) {
                if (bind != null) {
                    ++((MixinKeyBinding) (Object) bind).pressTime;
                }
            }
        }
    }

    /**
     * @author eigenraven
     * @reason Needed to replace a hashmap lookup with multimap iteration
     */
    @Overwrite
    public static void setKeyBindState(int keyCode, boolean pressed) {
        if (keyCode != 0) {
            for (KeyBinding bind : keybindMatcher.get(keyCode)) {
                if (bind != null) {
                    ((MixinKeyBinding) (Object) bind).pressed = pressed;
                }
            }
        }
    }

    @Inject(
            method = "Lnet/minecraft/client/settings/KeyBinding;resetKeyBindingArrayAndHash()V",
            at = @At("RETURN"),
            require = 1)
    private static void populateKeybindMatcherArray(CallbackInfo ci) {
        keybindMatcher.clear();
        for (KeyBinding binding : (List<KeyBinding>) keybindArray) {
            if (binding != null && binding.getKeyCode() != 0) {
                keybindMatcher.put(binding.getKeyCode(), binding);
            }
        }
    }

    @Inject(
            method = "Lnet/minecraft/client/settings/KeyBinding;<init>(Ljava/lang/String;ILjava/lang/String;)V",
            at = @At("RETURN"),
            require = 1)
    private void addMyselfInConstructor(String description, int keyCode, String category, CallbackInfo ci) {
        keybindMatcher.put(keyCode, (KeyBinding) (Object) this);
    }
}
