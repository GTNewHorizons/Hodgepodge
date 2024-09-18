package com.mitchej123.hodgepodge.util;

import java.util.Locale;
import java.util.regex.Pattern;

import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class ChatComponentItemTranslation extends ChatComponentStyle {

    private static final Pattern FORMATTING_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");

    private final Item item;

    public ChatComponentItemTranslation(Item item) {
        this.item = item;
    }

    @Override
    public String getUnformattedTextForChat() {
        String unlocalizedName = this.item.getUnlocalizedName() + ".name";
        String localizedName = StatCollector.translateToLocal(unlocalizedName);
        StringBuilder sb = new StringBuilder(getTextWithoutFormattingCodes(localizedName));
        for (IChatComponent sibling : this.getSiblings()) {
            sb.append(sibling.getUnformattedTextForChat());
        }
        return sb.toString();
    }

    @Override
    public IChatComponent createCopy() {
        ChatComponentItemTranslation component = new ChatComponentItemTranslation(this.item);
        for (IChatComponent sibling : this.getSiblings()) {
            component.appendSibling(sibling.createCopy());
        }
        return component;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + this.item.hashCode();
    }

    @Override
    public String toString() {
        return String.format(
                Locale.ROOT,
                "TranslatableItemComponent{item=%s, siblings=%s, style=%s}",
                this.item.delegate.name(),
                this.getSiblings(),
                this.getChatStyle());
    }

    private static String getTextWithoutFormattingCodes(String text) {
        return text == null ? null : FORMATTING_CODE_PATTERN.matcher(text).replaceAll("");
    }

}
