package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.GuiIngameForge;

import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.client.title.TitleAPI;

@Mixin(GuiIngameForge.class)
public class MixinGuiIngameForge_TitleRender {

    @Shadow
    private ScaledResolution res;

    @Shadow
    private FontRenderer fontrenderer;

    @Inject(
            method = "renderGameOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraftforge/client/GuiIngameForge;renderRecordOverlay(IIF)V",
                    shift = At.Shift.AFTER,
                    remap = false))
    private void hodgepodge$renderTitle(float partialTicks, boolean hasScreen, int mouseX, int mouseY,
            CallbackInfo ci) {
        IChatComponent title = TitleAPI.getTitle();
        int titleTime = TitleAPI.getTitleTime();
        if (title == null || titleTime <= 0) return;

        Minecraft mc = Minecraft.getMinecraft();
        mc.mcProfiler.startSection("titleAndSubtitle");

        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        float f = (float) titleTime - partialTicks;
        int fadeIn = TitleAPI.getFadeInTime();
        int stay = TitleAPI.getStayTime();
        int fadeOut = TitleAPI.getFadeOutTime();
        int alpha = 255;

        if (titleTime > fadeOut + stay) {
            float totalTime = (float) (fadeIn + stay + fadeOut);
            alpha = (int) ((totalTime - f) * 255.0F / (float) fadeIn);
        } else if (titleTime <= fadeOut) {
            alpha = (int) (f * 255.0F / (float) fadeOut);
        }

        alpha = MathHelper.clamp_int(alpha, 0, 255);
        if (alpha <= 8) {
            mc.mcProfiler.endSection();
            return;
        }

        int color = 0xFFFFFF | (alpha << 24);

        GL11.glPushMatrix();
        GL11.glTranslatef((float) (width / 2), (float) (height / 2), 0.0F);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glPushMatrix();
        GL11.glScalef(4.0F, 4.0F, 4.0F);
        String titleText = title.getFormattedText();
        int titleWidth = fontrenderer.getStringWidth(titleText);
        fontrenderer.drawStringWithShadow(titleText, -titleWidth / 2, -10, color);
        GL11.glPopMatrix();

        IChatComponent subtitle = TitleAPI.getSubtitle();
        if (subtitle != null) {
            GL11.glPushMatrix();
            GL11.glScalef(2.0F, 2.0F, 2.0F);
            String subText = subtitle.getFormattedText();
            int subWidth = fontrenderer.getStringWidth(subText);
            fontrenderer.drawStringWithShadow(subText, -subWidth / 2, 5, color);
            GL11.glPopMatrix();
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();

        mc.mcProfiler.endSection();
    }
}
