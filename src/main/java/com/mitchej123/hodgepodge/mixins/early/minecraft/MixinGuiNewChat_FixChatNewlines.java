package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.config.TweaksConfig;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat_FixChatNewlines {

    @Unique
    private boolean hodgepodge$suppressInnerDelete = false;

    @Shadow
    @Final
    private List<ChatLine> chatLines;

    @Shadow
    public abstract void deleteChatLine(int id);

    @Shadow
    private void func_146237_a(IChatComponent component, int id, int updateCounter, boolean displayed) {
        throw new AssertionError();
    }

    @Inject(
            method = "func_146237_a(Lnet/minecraft/util/IChatComponent;IIZ)V",
            at = @At("HEAD"),
            cancellable = true)
    private void hodgepodge$splitNewlines(IChatComponent original, int id, int updateCounter, boolean displayed,
            CallbackInfo ci) {
        if (original.getUnformattedText().indexOf('\n') < 0) return;

        if (id != 0 && !displayed) {
            this.deleteChatLine(id);
        }

        List<IChatComponent> lines = hodgepodge$splitOnNewlines(original);
        this.hodgepodge$suppressInnerDelete = true;
        try {
            for (IChatComponent line : lines) {
                this.func_146237_a(line, id, updateCounter, true);
            }
        } finally {
            this.hodgepodge$suppressInnerDelete = false;
        }

        if (!displayed) {
            this.chatLines.add(0, new ChatLine(updateCounter, original, id));
            while (this.chatLines.size() > TweaksConfig.chatLength) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }

        ci.cancel();
    }

    @Redirect(
            method = "func_146237_a(Lnet/minecraft/util/IChatComponent;IIZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;deleteChatLine(I)V"))
    private void hodgepodge$maybeSkipInnerDelete(GuiNewChat self, int id) {
        if (this.hodgepodge$suppressInnerDelete) return;
        self.deleteChatLine(id);
    }

    @Unique
    private static List<IChatComponent> hodgepodge$splitOnNewlines(IChatComponent original) {
        List<IChatComponent> lines = new ArrayList<>();
        ChatComponentText currentLine = new ChatComponentText("");
        StringBuilder lineRawText = new StringBuilder();
        String carryFormat = "";

        for (Iterator<IChatComponent> it = original.iterator(); it.hasNext();) {
            IChatComponent node = it.next();
            String text = node.getUnformattedTextForChat();
            if (text.isEmpty()) continue;

            String[] parts = text.split("\n", -1);
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) {
                    carryFormat = FontRenderer.getFormatFromString(lineRawText.toString());
                    lines.add(currentLine);
                    currentLine = new ChatComponentText("");
                    lineRawText.setLength(0);
                }
                if (!parts[i].isEmpty()) {
                    String effText = carryFormat.isEmpty() ? parts[i] : carryFormat + parts[i];
                    currentLine.appendSibling(hodgepodge$copyStyled(effText, node));
                    lineRawText.append(effText);
                    carryFormat = "";
                }
            }
        }
        lines.add(currentLine);
        return lines;
    }

    @Unique
    private static ChatComponentText hodgepodge$copyStyled(String text, IChatComponent source) {
        ChatComponentText piece = new ChatComponentText(text);
        piece.setChatStyle(source.getChatStyle().createShallowCopy());
        return piece;
    }
}
