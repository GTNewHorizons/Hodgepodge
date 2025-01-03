package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.item.Item;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.GameRegistry.UniqueIdentifier;

@Mixin(targets = "net.minecraft.client.gui.achievement.GuiStats$Stats")
public class MixinStats {

    @Shadow(aliases = "field_148214_q", remap = false)
    @Final
    GuiStats this$0; // enclosing instance

    @ModifyExpressionValue(
            at = @At(
                    target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I",
                    value = "INVOKE"),
            method = "func_148213_a")
    private int hodgepodge$max(int original, @Local Item item, @Share("modName") LocalRef<String> modNameRef) {
        UniqueIdentifier ui = GameRegistry.findUniqueIdentifierFor(item);
        if (ui == null) {
            return original;
        }
        ModContainer mod = Loader.instance().getIndexedModList().get(ui.modId);
        String modName = null;
        if (mod != null) {
            modName = mod.getName();
        }
        if (modName == null) {
            modName = "Minecraft";
        }
        modNameRef.set(modName);
        return Math.max(original, this.this$0.fontRendererObj.getStringWidth(modName));
    }

    @ModifyConstant(constant = @Constant(intValue = 8), method = "func_148213_a")
    private int hodgepodge$extendBottom(int original) {
        return original + 10;
    }

    @Inject(
            at = @At(
                    shift = Shift.AFTER,
                    target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;III)I",
                    value = "INVOKE"),
            method = "func_148213_a")
    private void hodgepodge$drawModName(CallbackInfo ci, @Local(ordinal = 2) int k, @Local(ordinal = 3) int l,
            @Share("modName") LocalRef<String> modNameRef) {
        String modName = modNameRef.get();
        if (modName == null) {
            return;
        }
        this.this$0.fontRendererObj.drawStringWithShadow(
                EnumChatFormatting.BLUE.toString() + EnumChatFormatting.ITALIC + modName,
                k,
                l + 10,
                -1);
    }

}
