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
import org.spongepowered.asm.mixin.injection.Redirect;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
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

    @WrapMethod(method = "func_146237_a(Lnet/minecraft/util/IChatComponent;IIZ)V")
    private void hodgepodge$splitNewlines(IChatComponent original, int id, int updateCounter, boolean displayed,
            Operation<Void> delegate) {
        if (original.getUnformattedText().indexOf('\n') < 0) {
            delegate.call(original, id, updateCounter, displayed);
            return;
        }

        // Delete-by-id must run exactly once even though we delegate per sub-line; the
        // @Redirect below suppresses the per-sub-call delete that would otherwise wipe
        // earlier sub-lines we just added to field_146253_i.
        if (id != 0 && !displayed) {
            this.deleteChatLine(id);
        }

        this.hodgepodge$suppressInnerDelete = true;
        try {
            for (IChatComponent line : hodgepodge$splitOnNewlines(original)) {
                // delegate.call re-enters the patched func_146237_a body per sub-line so
                // sibling mixins (CompactChat, LongerChat, FixColorWrapping) all fire as
                // if the sub-line had been printed directly.
                delegate.call(line, id, updateCounter, true);
            }
        } finally {
            this.hodgepodge$suppressInnerDelete = false;
        }

        // Keep the un-split component in chatLines so refreshChat() re-enters this wrap
        // and re-splits cleanly after e.g. a chat width change.
        if (!displayed) {
            this.chatLines.add(0, new ChatLine(updateCounter, original, id));
            while (this.chatLines.size() > TweaksConfig.chatLength) {
                this.chatLines.remove(this.chatLines.size() - 1);
            }
        }
    }

    @Redirect(
            method = "func_146237_a(Lnet/minecraft/util/IChatComponent;IIZ)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;deleteChatLine(I)V"))
    private void hodgepodge$maybeSkipInnerDelete(GuiNewChat self, int id) {
        if (this.hodgepodge$suppressInnerDelete) return;
        self.deleteChatLine(id);
    }

    /**
     * Splits a chat component tree on newline boundaries, preserving per-sibling style.
     *
     * <p>
     * Walks the component's iterator (which yields deep-copied nodes with resolved styles), splits each node's raw text
     * on {@code \n}, and flushes the current line buffer whenever a {@code \n} is crossed. Trailing format codes of the
     * just-flushed line are carried over to the first non-empty segment of the next line so sticky codes ({@code §a},
     * {@code §l}, ...) continue — the same continuation vanilla's own word-wrap applies across wrapped sub-lines.
     *
     * <p>
     * Per-sibling {@code ChatStyle} (including click/hover events) is preserved by shallow-copying onto each rebuilt
     * piece — the same construction vanilla {@code func_146237_a} uses when building its wrapped sub-pieces.
     */
    @Unique
    private static List<IChatComponent> hodgepodge$splitOnNewlines(IChatComponent original) {
        List<IChatComponent> lines = new ArrayList<>();
        ChatComponentText currentLine = new ChatComponentText("");
        StringBuilder currentLineRaw = new StringBuilder();
        // Format codes carried from the last flushed line onto the next line's first
        // non-empty segment.
        String formatCarry = "";

        for (Iterator<IChatComponent> it = original.iterator(); it.hasNext();) {
            IChatComponent node = it.next();
            String nodeText = node.getUnformattedTextForChat();
            if (nodeText.isEmpty()) continue;

            // limit=-1 preserves trailing empty segments so "abc\n" -> ["abc", ""].
            String[] segments = nodeText.split("\n", -1);
            for (int i = 0; i < segments.length; i++) {
                boolean startsNewLine = i > 0;
                if (startsNewLine) {
                    formatCarry = FontRenderer.getFormatFromString(currentLineRaw.toString());
                    lines.add(currentLine);
                    currentLine = new ChatComponentText("");
                    currentLineRaw.setLength(0);
                }

                String segment = segments[i];
                if (segment.isEmpty()) continue;

                String segmentWithCarry = formatCarry.isEmpty() ? segment : formatCarry + segment;
                currentLine.appendSibling(hodgepodge$copyStyled(segmentWithCarry, node));
                currentLineRaw.append(segmentWithCarry);
                formatCarry = "";
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
