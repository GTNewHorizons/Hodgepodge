package com.mitchej123.hodgepodge.mixins.late.betterhud;

import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import tk.nukeduck.hud.element.ExtraGuiElementArmorBars;

@Mixin(value = ExtraGuiElementArmorBars.class, remap = false)
public abstract class MixinSkullDurabilityDisplay {

    /**
     * Ensure the drawn damage bar doesn't display invalid values
     */
    @ModifyArg(
            method = "render(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/gui/ScaledResolution;Ltk/nukeduck/hud/util/StringManager;Ltk/nukeduck/hud/util/LayoutManager;)V",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Ltk/nukeduck/hud/util/RenderUtil;drawProgressBar(IIIIF)V",
                    remap = false))
    private float fixProgressCheck(float input) {
        return (input < 0.0f || input > 1.0f) ? 1.0f : input;
    }

    /**
     * Fix text for itemSkulls displaying negative numbers
     */
    @Inject(
            method = "generateText(Lnet/minecraft/item/ItemStack;)Ljava/lang/String;",
            remap = false,
            at = @At(value = "HEAD", remap = false),
            cancellable = true)
    private void fixSkullText(ItemStack itemStack, CallbackInfoReturnable<String> cir) {
        if (itemStack.getItem() instanceof ItemSkull) {
            cir.setReturnValue(itemStack.getDisplayName());
        }
    }
}
