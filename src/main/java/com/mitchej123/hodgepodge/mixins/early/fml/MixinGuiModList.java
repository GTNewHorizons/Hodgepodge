package com.mitchej123.hodgepodge.mixins.early.fml;

import java.net.URI;
import java.net.URISyntaxException;

import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.gtnewhorizon.gtnhlib.util.FilesUtil;

import cpw.mods.fml.client.GuiModList;

@Mixin(GuiModList.class)
public abstract class MixinGuiModList extends GuiScreen {

    int urlX = 0;
    int urlY = 0;
    String urlStr;

    public void handleMouseInput() {
        super.handleMouseInput();
        int clickX = Mouse.getEventX() * this.width / this.mc.displayWidth;
        int clickY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int eventButton = Mouse.getEventButton();

        if (Mouse.getEventButtonState() && eventButton == 0) {
            if (this.mc.gameSettings.touchscreen) {
                return;
            }

            if (urlStr != null) {
                int height = this.fontRendererObj.FONT_HEIGHT;
                int width = this.fontRendererObj.getStringWidth(urlStr);
                if (clickX >= urlX && clickX <= urlX + width && clickY >= urlY && clickY <= urlY + height) {
                    try {
                        FilesUtil.openUri(new URI(urlStr));
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @Redirect(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lcpw/mods/fml/client/GuiModList;drawLine(Ljava/lang/String;II)I"))
    private int redirectDrawLine(GuiModList instance, String line, int offset, int shifty) {
        if (line != null && line.startsWith("URL: ")) {
            urlX = offset + this.fontRendererObj.getStringWidth("URL: ");
            urlY = shifty;
            urlStr = line.substring("URL: ".length());

            line = line.replace("URL: ", "URL: §9§n");
        }
        return instance.drawLine(line, offset, shifty);
    }
}
