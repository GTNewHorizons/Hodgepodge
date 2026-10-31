package com.mitchej123.hodgepodge.mixins.early.fml;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.mitchej123.hodgepodge.client.scrolllist.IScrollList;
import com.mitchej123.hodgepodge.client.scrolllist.ScrollListInput;

import cpw.mods.fml.client.GuiScrollingList;

@Mixin(GuiScrollingList.class)
public abstract class MixinGuiScrollingList_ScrollList implements IScrollList {

    @Shadow(remap = false)
    @Final
    protected int listWidth;
    @Shadow(remap = false)
    @Final
    protected int top;
    @Shadow(remap = false)
    @Final
    protected int bottom;
    @Shadow(remap = false)
    @Final
    protected int left;
    @Shadow(remap = false)
    @Final
    protected int slotHeight;
    @Shadow(remap = false)
    @Final
    private int right;
    @Shadow(remap = false)
    private float scrollDistance;
    @Shadow(remap = false)
    private int selectedIndex;
    @Shadow(remap = false)
    private long lastClickTime;

    @Unique
    private final ScrollListInput.State hodgepodge$state = new ScrollListInput.State();

    @Shadow(remap = false)
    protected abstract int getSize();

    @Shadow(remap = false)
    protected abstract int getContentHeight();

    @Shadow(remap = false)
    protected abstract void elementClicked(int index, boolean doubleClick);

    @Shadow(remap = false)
    protected abstract void func_27255_a(int x, int y);

    @Shadow(remap = false)
    private void applyScrollLimits() {}

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
        return left + listWidth - 6;
    }

    @Override
    public int hodgepodge$getContentHeight() {
        return getContentHeight();
    }

    @Override
    public float hodgepodge$getScroll() {
        return scrollDistance;
    }

    @Override
    public void hodgepodge$setScroll(float scroll) {
        scrollDistance = scroll;
        applyScrollLimits();
    }

    @Override
    public boolean hodgepodge$canClick() {
        return true;
    }

    @Override
    public boolean hodgepodge$clickElement(int x, int y) {
        if (x < left || x > left + listWidth - 7) return true;
        int headerHeight = getContentHeight() - getSize() * slotHeight;
        int offset = y - top - headerHeight + (int) scrollDistance - 4;
        int row = offset / slotHeight;
        if (row >= 0 && offset >= 0 && row < getSize()) {
            boolean doubleClick = row == selectedIndex && System.currentTimeMillis() - lastClickTime < 250L;
            elementClicked(row, doubleClick);
            selectedIndex = row;
            lastClickTime = System.currentTimeMillis();
            return true;
        }
        if (offset < 0) {
            func_27255_a(x - left, y - top + (int) scrollDistance - 4);
            return false;
        }
        return true;
    }

    @Override
    public ScrollListInput.State hodgepodge$getState() {
        return hodgepodge$state;
    }
}
