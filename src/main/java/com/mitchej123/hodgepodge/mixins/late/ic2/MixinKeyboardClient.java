package com.mitchej123.hodgepodge.mixins.late.ic2;

import ic2.core.util.KeyboardClient;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyboardClient.class)
public class MixinKeyboardClient {
    @Shadow(remap = false)
    private final KeyBinding altKey = new KeyBinding("ALT Key", Keyboard.KEY_NONE, "IC2");

    @Shadow(remap = false)
    private final KeyBinding boostKey = new KeyBinding("Boost Key", Keyboard.KEY_NONE, "IC2");

    @Shadow(remap = false)
    private final KeyBinding modeSwitchKey = new KeyBinding("Mode Switch Key", Keyboard.KEY_NONE, "IC2");

    @Shadow(remap = false)
    private final KeyBinding sideinventoryKey = new KeyBinding("Side Inventory Key", Keyboard.KEY_NONE, "IC2");

    @Shadow(remap = false)
    private final KeyBinding expandinfo = new KeyBinding("Hub Expand Key", Keyboard.KEY_NONE, "IC2");
}
