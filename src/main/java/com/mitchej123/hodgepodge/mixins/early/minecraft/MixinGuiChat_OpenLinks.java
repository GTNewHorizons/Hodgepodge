package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.net.URI;

import net.minecraft.client.gui.GuiChat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.gtnewhorizon.gtnhlib.util.FilesUtil;

@Mixin(GuiChat.class)
public class MixinGuiChat_OpenLinks {

    /**
     * @author Alexdoru
     * @reason The Vanilla method doesn't work on some OS
     */
    @Overwrite
    private void func_146407_a(URI uri) {
        FilesUtil.openUri(uri);
    }

}
