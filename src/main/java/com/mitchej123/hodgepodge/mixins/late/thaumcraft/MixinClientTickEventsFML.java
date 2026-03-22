package com.mitchej123.hodgepodge.mixins.late.thaumcraft;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mitchej123.hodgepodge.mixins.early.minecraft.AccessorGuiContainer;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.lib.ClientTickEventsFML;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.research.ScanManager;

@Mixin(value = ClientTickEventsFML.class, remap = false, priority = 999)
public class MixinClientTickEventsFML {

    /**
     * @author Sisyphussy
     * @reason Fix a bug with the hovered slot being wrong + make it more performant
     */
    @Overwrite
    public void renderAspectsInGui(GuiContainer gui, EntityPlayer player) {
        final Slot slot = ((AccessorGuiContainer) gui).getHoveredSlot();
        if (slot == null) return;
        final ItemStack stack = slot.getStack();
        if (stack == null) return;
        final String name = player.getCommandSenderName();

        int h = ScanManager.generateItemHash(stack.getItem(), stack.getItemDamage());
        List<String> list = Thaumcraft.proxy.getScannedObjects().get(name);
        if (list != null && (list.contains("@" + h) || list.contains("#" + h))) {
            AspectList tags = ThaumcraftCraftingManager.getObjectTags(stack);
            tags = ThaumcraftCraftingManager.getBonusTags(stack, tags);
            if (tags.size() > 0) {
                Minecraft mc = Minecraft.getMinecraft();
                ScaledResolution resolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
                final int width = resolution.getScaledWidth();
                final int height = resolution.getScaledHeight();
                final int mouseX = Mouse.getX() * width / mc.displayWidth;
                final int mouseY = height - Mouse.getY() * height / mc.displayHeight - 1;

                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                final int shiftX, shiftY;
                if (Thaumcraft.instance.aspectShift) {
                    shiftX = -16;
                    shiftY = -16;
                } else {
                    shiftX = -8;
                    shiftY = -8;
                }
                final int y = mouseY + 7 - 33;
                final float zLevel = UtilsFX.getGuiZLevel(gui);

                int x = mouseX + 17;
                for (Aspect tag : tags.getAspectsSortedAmount()) {
                    if (tag != null) {

                        UtilsFX.bindTexture("textures/aspects/_back.png");
                        GL11.glPushMatrix();
                        GL11.glEnable(GL11.GL_BLEND);
                        GL11.glTranslated(x + shiftX - 2, y + shiftY - 2, 0.0D);
                        GL11.glScaled(1.25D, 1.25D, 0.0D);
                        UtilsFX.drawTexturedQuadFull(0, 0, zLevel);
                        GL11.glPopMatrix();

                        if (Thaumcraft.proxy.playerKnowledge.hasDiscoveredAspect(name, tag)) {
                            UtilsFX.drawTag(x + shiftX, y + shiftY, tag, (float) tags.getAmount(tag), 0, zLevel);
                        } else {
                            UtilsFX.bindTexture("textures/aspects/_unknown.png");
                            UtilsFX.drawTexturedQuadFull(x + shiftX, y + shiftY, zLevel);
                        }

                        x += 18;
                    }
                }

                GL11.glDisable(GL11.GL_BLEND);

                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }
        }
    }
}
