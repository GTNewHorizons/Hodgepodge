package com.mitchej123.hodgepodge.mixins.early.fml;

import java.net.URI;
import java.net.URISyntaxException;

import net.minecraft.client.gui.GuiScreen;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import com.gtnewhorizon.gtnhlib.util.FilesUtil;

import cpw.mods.fml.client.GuiModList;

@Mixin(GuiModList.class)
public abstract class MixinGuiModList extends GuiScreen {

    @Unique
    int hodgepodge$urlX = 0;

    @Unique
    int hodgepodge$urlY = 0;

    @Unique
    String hodgepodge$urlStr;

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (hodgepodge$urlStr != null) {
            int height = this.fontRendererObj.FONT_HEIGHT;
            int width = this.fontRendererObj.getStringWidth(hodgepodge$urlStr);
            if (mouseX >= hodgepodge$urlX && mouseX <= hodgepodge$urlX + width
                    && mouseY >= hodgepodge$urlY
                    && mouseY <= hodgepodge$urlY + height) {
                try {
                    FilesUtil.openUri(new URI(hodgepodge$urlStr));
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @ModifyArgs(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lcpw/mods/fml/client/GuiModList;drawLine(Ljava/lang/String;II)I"),
            remap = false)
    private void replaceDrawLineArgs(Args args) {
        String line = args.get(0);
        int offset = args.get(1);
        int shifty = args.get(2);
        if (line != null && line.startsWith("URL: ")) {
            hodgepodge$urlX = offset + this.fontRendererObj.getStringWidth("URL: ");
            hodgepodge$urlY = shifty;
            hodgepodge$urlStr = line.substring("URL: ".length());

            args.set(0, line.replace("URL: ", "URL: §9§n"));
        }
    }
}
