package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiSlot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mitchej123.hodgepodge.client.scrolllist.IScrollList;
import com.mitchej123.hodgepodge.client.scrolllist.ScrollListInput;

@Mixin(GuiSlot.class)
public abstract class MixinGuiSlot_ScrollInput implements IScrollList {

    @Shadow
    public int width;
    @Shadow
    public int top;
    @Shadow
    public int bottom;
    @Shadow
    public int left;
    @Shadow
    public int right;
    @Shadow
    public int headerPadding;
    @Shadow
    private float amountScrolled;
    @Shadow
    private int selectedElement;
    @Shadow
    private long lastClicked;

    @Unique
    private final ScrollListInput.State hodgepodge$state = new ScrollListInput.State();

    @Shadow
    protected abstract int getContentHeight();

    @Shadow
    protected abstract int getScrollBarX();

    @Shadow
    public abstract int getListWidth();

    @Shadow
    protected abstract void elementClicked(int index, boolean doubleClick, int x, int y);

    @Shadow
    protected abstract void func_148132_a(int x, int y);

    @Shadow
    public abstract int func_148124_c(int x, int y);

    @Shadow
    public abstract boolean func_148125_i();

    @Shadow
    private void bindAmountScrolled() {}

    @Redirect(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z", remap = false))
    private boolean hodgepodge$noPolledButton(int button) {
        return false;
    }

    @Redirect(
            method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z", remap = false))
    private boolean hodgepodge$noPolledEvents() {
        return false;
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void hodgepodge$onDraw(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        ScrollListInput.onDraw(this, mouseY);
    }

    @Override
    public int hodgepodge$getTop() {
        return top;
    }

    @Override
    public int hodgepodge$getBottom() {
        return bottom;
    }

    @Override
    public int hodgepodge$getLeft() {
        return left;
    }

    @Override
    public int hodgepodge$getRight() {
        return right;
    }

    @Override
    public int hodgepodge$getBarX() {
        return getScrollBarX();
    }

    @Override
    public int hodgepodge$getContentHeight() {
        return getContentHeight();
    }

    @Override
    public float hodgepodge$getScroll() {
        return amountScrolled;
    }

    @Override
    public void hodgepodge$setScroll(float scroll) {
        amountScrolled = scroll;
        bindAmountScrolled();
    }

    @Override
    public boolean hodgepodge$canClick() {
        return func_148125_i();
    }

    @Override
    public boolean hodgepodge$clickElement(int x, int y) {
        int row = func_148124_c(x, y);
        if (row >= 0) {
            boolean doubleClick = row == selectedElement && Minecraft.getSystemTime() - lastClicked < 250L;
            elementClicked(row, doubleClick, x, y);
            selectedElement = row;
            lastClicked = Minecraft.getSystemTime();
            return true;
        }
        int listLeft = left + width / 2 - getListWidth() / 2;
        int listRight = left + width / 2 + getListWidth() / 2;
        if (y - top - headerPadding + (int) amountScrolled - 4 < 0 && x >= listLeft && x <= listRight) {
            func_148132_a(x - listLeft, y - top + (int) amountScrolled - 4);
            return false;
        }
        return true;
    }

    @Override
    public ScrollListInput.State hodgepodge$getState() {
        return hodgepodge$state;
    }
}
