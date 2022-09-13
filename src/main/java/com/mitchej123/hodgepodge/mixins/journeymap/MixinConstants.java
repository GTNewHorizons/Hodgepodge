package com.mitchej123.hodgepodge.mixins.journeymap;

import journeymap.client.Constants;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(Constants.class)
public class MixinConstants {

    /**
     * @author Alexdoru
     * @reason Prevent unbinded keybinds from triggering when pressing certain keys
     */
    @Overwrite(remap = false)
    public static boolean isPressed(KeyBinding keyBinding) {
        return keyBinding.isPressed();
    }
}
