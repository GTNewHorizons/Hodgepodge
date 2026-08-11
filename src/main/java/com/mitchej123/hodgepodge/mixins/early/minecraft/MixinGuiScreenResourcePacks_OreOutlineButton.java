package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Collections;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.config.ConfigurationManager;
import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(GuiScreenResourcePacks.class)
public class MixinGuiScreenResourcePacks_OreOutlineButton extends GuiScreen {

    @Unique
    private static final ResourceLocation hodgepodge$icon = new ResourceLocation(
            "hodgepodge",
            "textures/gui/outline_button.png");

    @Unique
    private static final int hodgepodge$buttonId = 9001;

    @Inject(method = "initGui", at = @At("TAIL"))
    private void hodgepodge$addOreOutlineButton(CallbackInfo ci) {
        this.buttonList.add(new GuiButton(hodgepodge$buttonId, this.width / 2 - 204, this.height - 48, 20, 20, ""));
    }

    @Inject(method = "actionPerformed", at = @At("HEAD"))
    private void hodgepodge$onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.id == hodgepodge$buttonId) {
            TweaksConfig.oreOutlineEnabled = !TweaksConfig.oreOutlineEnabled;
            ConfigurationManager.save(TweaksConfig.class);
        }
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void hodgepodge$drawOreOutlineIcon(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        int iconX = this.width / 2 - 202;
        int iconY = this.height - 46;

        this.mc.getTextureManager().bindTexture(hodgepodge$icon);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Gui.func_146110_a(iconX, iconY, 0, TweaksConfig.oreOutlineEnabled ? 16 : 0, 16, 16, 16, 32);

        int buttonX = this.width / 2 - 204;
        int buttonY = this.height - 48;
        if (mouseX >= buttonX && mouseX < buttonX + 20 && mouseY >= buttonY && mouseY < buttonY + 20) {
            String key = TweaksConfig.oreOutlineEnabled ? "hodgepodge.resourcepacks.ore_outline.enabled"
                    : "hodgepodge.resourcepacks.ore_outline.disabled";
            this.drawHoveringText(Collections.singletonList(I18n.format(key)), mouseX, mouseY, this.fontRendererObj);
        }
    }
}
