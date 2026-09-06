package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(KeyBinding.class)
public class MixinKeyBinding_FixComparison {

    @Shadow
    @Final
    private String keyDescription;
    @Shadow
    @Final
    private String keyCategory;

    /**
     * @author Algent
     * @reason Fix controls menu crash when keybind categories share a localized name which can crash the game.
     */
    @Overwrite(remap = false)
    public int compareTo(KeyBinding other) {
        final MixinKeyBinding_FixComparison mixinOther = (MixinKeyBinding_FixComparison) (Object) other;
        int result = I18n.format(this.keyCategory).compareTo(I18n.format(mixinOther.keyCategory));
        if (result == 0) {
            result = this.keyCategory.compareTo(mixinOther.keyCategory);
            if (result == 0) {
                result = I18n.format(this.keyDescription).compareTo(I18n.format(mixinOther.keyDescription));
            }
        }
        return result;
    }
}
