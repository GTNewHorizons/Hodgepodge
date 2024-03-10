package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mitchej123.hodgepodge.mixins.interfaces.KeyBindingExt;

@Mixin(KeyBinding.class)
public class MixinKeyBinding implements KeyBindingExt {

    @Shadow
    @Final
    private static List<KeyBinding> keybindArray;
    @Shadow
    private int pressTime;
    @Shadow
    private boolean pressed;
    @Unique
    private static final Multimap<Integer, KeyBinding> hodgepodge$KEYBIND_MULTIMAP = ArrayListMultimap.create();

    /**
     * @author eigenraven
     * @reason Needed to replace a hashmap lookup with multimap iteration
     */
    @Overwrite
    public static void onTick(int keyCode) {
        if (keyCode != 0) {
            for (KeyBinding bind : hodgepodge$KEYBIND_MULTIMAP.get(keyCode)) {
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
            for (KeyBinding bind : hodgepodge$KEYBIND_MULTIMAP.get(keyCode)) {
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
    private static void hodgepodge$populateKeybindMatcherArray(CallbackInfo ci) {
        hodgepodge$KEYBIND_MULTIMAP.clear();
        for (KeyBinding binding : (List<KeyBinding>) keybindArray) {
            if (binding != null && binding.getKeyCode() != 0) {
                hodgepodge$KEYBIND_MULTIMAP.put(binding.getKeyCode(), binding);
            }
        }
    }

    @Inject(
            method = "Lnet/minecraft/client/settings/KeyBinding;<init>(Ljava/lang/String;ILjava/lang/String;)V",
            at = @At("RETURN"),
            require = 1)
    private void hodgepodge$addMyselfInConstructor(String description, int keyCode, String category, CallbackInfo ci) {
        hodgepodge$KEYBIND_MULTIMAP.put(keyCode, (KeyBinding) (Object) this);
    }

    @Override
    public void hodgepodge$updateKeyStates() {
        for (KeyBinding keyBinding : hodgepodge$KEYBIND_MULTIMAP.values()) {
            try {
                final int keyCode = keyBinding.getKeyCode();
                KeyBinding.setKeyBindState(keyCode, keyCode < 256 && Keyboard.isKeyDown(keyCode));
            } catch (IndexOutOfBoundsException ignored) {}
        }
    }
}
