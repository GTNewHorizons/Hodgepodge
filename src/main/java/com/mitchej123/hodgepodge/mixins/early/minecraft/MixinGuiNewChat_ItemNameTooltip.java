package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.chat.customcomponents.ChatComponentItemName;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mixin(GuiNewChat.class)
public class MixinGuiNewChat_ItemNameTooltip {

    // Maps displayed text (e.g. "[Chest]") -> ChatComponentItemName for tooltip lookup.
    // Populated when a message containing a ChatComponentItemName is added to chat.
    @Unique
    private final Map<String, ChatComponentItemName> hodgepodge$itemNameCache = new HashMap<>();

    @Inject(method = "func_146237_a", at = @At("HEAD"))
    private void hodgepodge$cacheItemName(IChatComponent component, int lineId, int updateCounter, boolean refresh,
            CallbackInfo ci) {
        ChatComponentItemName found = hodgepodge$findItemName(component);
        if (found != null) {
            this.hodgepodge$itemNameCache.put(found.getUnformattedTextForChat(), found);
        }
    }

    // Intercept the component returned by func_146236_a (getChatComponent under cursor).
    // If the line text contains a cached "[ItemName]" token, return the ChatComponentItemName
    // so the tooltip mixin can render the item tooltip.
    @ModifyReturnValue(method = "func_146236_a", at = @At("RETURN"))
    private IChatComponent hodgepodge$resolveCustomComponent(IChatComponent original) {
        if (original == null || this.hodgepodge$itemNameCache.isEmpty()) return original;
        String text = original.getUnformattedText();
        for (Map.Entry<String, ChatComponentItemName> entry : this.hodgepodge$itemNameCache.entrySet()) {
            if (text.contains(entry.getKey())) return entry.getValue();
        }
        return original;
    }

    // Recursively search for a ChatComponentItemName using the component's iterator,
    // which correctly traverses ChatComponentTranslation format args as well as siblings.
    @Unique
    private static ChatComponentItemName hodgepodge$findItemName(IChatComponent component) {
        if (component == null) return null;
        for (Object child : component) {
            if (child instanceof ChatComponentItemName c) return c;
        }
        return null;
    }
}
