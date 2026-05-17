package com.mitchej123.hodgepodge.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDifficultyLockButton extends GuiButton {

    private static final ResourceLocation LOCK_TEXTURE = new ResourceLocation(
            "hodgepodge",
            "textures/gui/lock_icons.png");

    private boolean locked;

    public GuiDifficultyLockButton(int buttonId, int x, int y) {
        super(buttonId, x, y, 20, 20, "");
    }

    public boolean isLocked() {
        return !this.locked;
    }

    public void setLocked(boolean lockedIn) {
        this.locked = lockedIn;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.visible) return;

        mc.getTextureManager().bindTexture(LOCK_TEXTURE);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        final boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition
                && mouseX < this.xPosition + this.width
                && mouseY < this.yPosition + this.height;

        final Icon icon = Icon.get(this.locked, this.enabled, hovered);

        func_146110_a(this.xPosition, this.yPosition, icon.getU(), icon.getV(), this.width, this.height, 64, 64);
    }

    @SideOnly(Side.CLIENT)
    enum Icon {

        LOCKED(0, 0),
        LOCKED_HOVER(0, 20),
        LOCKED_DISABLED(0, 40),
        UNLOCKED(20, 0),
        UNLOCKED_HOVER(20, 20),
        UNLOCKED_DISABLED(20, 40);

        private final int u;
        private final int v;

        Icon(int u, int v) {
            this.u = u;
            this.v = v;
        }

        public int getU() {
            return this.u;
        }

        public int getV() {
            return this.v;
        }

        public static Icon get(boolean locked, boolean enabled, boolean hovered) {
            final int state = (locked ? 0 : 3) + (!enabled ? 2 : hovered ? 1 : 0);
            return switch (state) {
                case 1 -> LOCKED_HOVER;
                case 2 -> LOCKED_DISABLED;
                case 3 -> UNLOCKED;
                case 4 -> UNLOCKED_HOVER;
                case 5 -> UNLOCKED_DISABLED;
                default -> LOCKED;
            };
        }
    }
}
