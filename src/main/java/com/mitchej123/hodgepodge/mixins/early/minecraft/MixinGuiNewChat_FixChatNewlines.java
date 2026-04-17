package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_FixChatNewlines {

    @Inject(method = "printChatMessageWithOptionalDeletion", at = @At("HEAD"), cancellable = true)
    private void hodgepodge$splitNewlines(IChatComponent component, int id, CallbackInfo ci) {
        String formatted = component.getFormattedText();
        if (formatted.indexOf('\n') < 0) return;

        List<IChatComponent> lines = hodgepodge$splitOnNewlines(formatted);
        for (int i = 0; i < lines.size(); i++) {
            ((GuiNewChat) (Object) this).printChatMessageWithOptionalDeletion(lines.get(i), i == 0 ? id : 0);
        }
        ci.cancel();
    }

    @Unique
    private static List<IChatComponent> hodgepodge$splitOnNewlines(String formatted) {
        String[] parts = formatted.split("\n", -1);
        List<IChatComponent> lines = new ArrayList<>();
        String carryFormat = "";

        for (String part : parts) {
            String fullLine = carryFormat + part;
            lines.add(new ChatComponentText(fullLine));
            carryFormat = FontRenderer.getFormatFromString(fullLine);
        }

        return lines;
    }
}
