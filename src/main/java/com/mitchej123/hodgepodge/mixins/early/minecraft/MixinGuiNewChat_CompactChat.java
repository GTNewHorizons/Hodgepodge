package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.mitchej123.hodgepodge.client.chat.ChatHandler;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_CompactChat {

    @Shadow
    @Final
    private List<ChatLine> chatLines;

    @Shadow
    @Final
    private List<ChatLine> field_146253_i; // drawnChatLines

    @Unique
    private boolean hodgepodge$deleteMessage;

    @Inject(method = "func_146237_a", at = @At("HEAD"))
    private void hodgepodge$compactChat(IChatComponent imsg, int chatLineId, int updateCounter, boolean refresh,
            CallbackInfo ci) {
        this.hodgepodge$deleteMessage = !refresh && ChatHandler.tryCompactMessage(imsg, this.chatLines)
                && chatLineId == 0;
    }

    @Inject(
            method = "func_146237_a",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;getChatOpen()Z"),
            locals = LocalCapture.CAPTURE_FAILSOFT)
    private void hodgepodge$deletePrevious(IChatComponent imsg, int chatLineId, int updateCounter, boolean refresh,
            CallbackInfo ci, int k, int l, ChatComponentText chatcomponenttext,
            ArrayList<ChatComponentText> arraylist) {
        if (!this.hodgepodge$deleteMessage) return;
        if (this.chatLines.isEmpty()) return;
        this.chatLines.remove(0);
        for (int i = 0; i < arraylist.size(); i++) {
            if (!this.field_146253_i.isEmpty()) {
                this.field_146253_i.remove(0);
            }
        }
    }

}
