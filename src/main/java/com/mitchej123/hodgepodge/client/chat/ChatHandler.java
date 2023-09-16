package com.mitchej123.hodgepodge.client.chat;

import java.util.List;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.util.IChatComponent;

public class ChatHandler {

    private static int previousCount = 1;

    /**
     * Returns true if it compacts the message
     */
    public static boolean tryCompactMessage(IChatComponent imsg, @SuppressWarnings("rawtypes") List chatLines) {
        if (chatLines.isEmpty()) return false;
        final ChatLine chatLine = (ChatLine) chatLines.get(0);
        final IChatComponent prevMsg = chatLine.func_151461_a();
        if (areMessagesIdentical(imsg, prevMsg)) {
            imsg.appendSibling(new ChatComponentCount(previousCount + 1));
            return true;
        }
        return false;
    }

    private static boolean areMessagesIdentical(IChatComponent imsg, IChatComponent prevMsg) {
        final int size1 = imsg.getSiblings().size();
        final int size2 = prevMsg.getSiblings().size();
        if (size1 == size2) {
            final boolean equals = imsg.equals(prevMsg);
            if (equals) previousCount = 1;
            return equals;
        }
        if (size1 + 1 != size2) {
            return false;
        }
        if (!(prevMsg.getSiblings().get(size2 - 1) instanceof ChatComponentCount)) {
            return false;
        }
        final Object removed = prevMsg.getSiblings().remove(size2 - 1);
        final boolean equals = imsg.equals(prevMsg);
        // noinspection unchecked
        prevMsg.getSiblings().add(removed);
        if (equals) {
            previousCount = ((ChatComponentCount) removed).getCount();
        }
        return equals;
    }

}
