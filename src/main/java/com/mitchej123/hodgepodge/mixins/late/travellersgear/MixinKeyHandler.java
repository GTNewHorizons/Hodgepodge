package com.mitchej123.hodgepodge.mixins.late.travellersgear;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import travellersgear.TravellersGear;
import travellersgear.client.KeyHandler;

@Mixin(KeyHandler.class)
public class MixinKeyHandler {
    @Shadow(remap = false)
    public static KeyBinding openInventory =
            new KeyBinding("TG.keybind.openInv", Keyboard.KEY_NONE, TravellersGear.MODNAME);

    @Shadow(remap = false)
    public static KeyBinding activeAbilitiesWheel =
            new KeyBinding("TG.keybind.activeaAbilities", Keyboard.KEY_NONE, TravellersGear.MODNAME);
}
