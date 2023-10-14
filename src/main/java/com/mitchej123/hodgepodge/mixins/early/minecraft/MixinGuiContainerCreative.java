package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumChatFormatting;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.WrapWithCondition;

@Mixin(GuiContainerCreative.class)
public abstract class MixinGuiContainerCreative extends GuiContainer {

    @Shadow
    private static int selectedTabIndex;

    public MixinGuiContainerCreative(Container p_i1072_1_) {
        super(p_i1072_1_);
    }

    @Inject(
            method = "drawScreen",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V",
                    shift = At.Shift.BEFORE,
                    remap = false))
    private void hodgepodge$drawMessageInfoInTab(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (selectedTabIndex == CreativeTabs.tabAllSearch.getTabIndex()) {
            final String text = EnumChatFormatting.RED + "Use NEI to browse items!";
            final int xOffset = mc.fontRenderer.getStringWidth(text) / 2;
            mc.fontRenderer.drawStringWithShadow(
                    text,
                    this.guiLeft + this.xSize / 2 - xOffset,
                    this.guiTop + this.ySize / 2 - mc.fontRenderer.FONT_HEIGHT,
                    0xFFFFFF);
        }
    }

    @ModifyExpressionValue(
            method = "keyTyped",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/settings/GameSettings;isKeyDown(Lnet/minecraft/client/settings/KeyBinding;)Z"))
    private boolean hodgepodge$disableChatKeybind(boolean original) {
        return false;
    }

    @Inject(
            method = "updateCreativeSearch",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/item/Item;itemRegistry:Lnet/minecraft/util/RegistryNamespaced;",
                    shift = At.Shift.BEFORE),
            cancellable = true)
    private void hodgepodge$disableCreativeSearch(CallbackInfo ci) {
        ci.cancel();
    }

    @WrapWithCondition(
            method = "setCurrentCreativeTab",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/creativetab/CreativeTabs;displayAllReleventItems(Ljava/util/List;)V"))
    private boolean hodgepodge$removeAllItemsFromTab(CreativeTabs tab, @SuppressWarnings("rawtypes") List list) {
        return tab != CreativeTabs.tabAllSearch;
    }

}
