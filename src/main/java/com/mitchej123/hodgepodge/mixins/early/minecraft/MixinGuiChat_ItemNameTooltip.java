package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.IChatComponent;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.gtnewhorizon.gtnhlib.chat.customcomponents.ChatComponentItemName;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mixin(GuiChat.class)
public abstract class MixinGuiChat_ItemNameTooltip extends GuiScreen {

    // Render item tooltip when hovering over a ChatComponentItemName in chat.
    // ChatComponentItemName carries the full ItemStack (safe binary serialization),
    // so we render the tooltip directly without NBT toString roundtrip that caused GTNH#21933, GTNH#21940.
    @Inject(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiScreen;drawScreen(IIF)V"))
    private void hodgepodge$renderItemNameTooltip(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        int rawX = Mouse.getX();
        int rawY = Mouse.getY();
        IChatComponent component = this.mc.ingameGUI.getChatGUI().func_146236_a(rawX, rawY);
        if (component instanceof ChatComponentItemName chatComp && chatComp.stack != null) {
            this.renderToolTip(chatComp.stack, mouseX, mouseY);
            GL11.glDisable(GL11.GL_LIGHTING);
        }
    }
}
