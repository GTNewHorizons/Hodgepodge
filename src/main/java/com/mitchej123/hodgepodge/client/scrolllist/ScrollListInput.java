package com.mitchej123.hodgepodge.client.scrolllist;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;

public final class ScrollListInput {

    private static final int BAR_WIDTH = 6;
    private static final float WHEEL_STEP = 30f;
    private static final float WHEEL_EASE_MS = 60f;
    private static final long STALE_MS = 1000L;
    private static final List<IScrollList> LISTS = new ArrayList<>();
    private static GuiScreen screen;

    private ScrollListInput() {}

    private enum Mode {
        NONE,
        BAR,
        CONTENT
    }

    public static final class State {

        private Mode mode = Mode.NONE;
        private int grabOffset;
        private int anchorY;
        private float anchorScroll;
        private long lastDraw;
        private boolean easing;
        private float easeTarget;
    }

    /** Called at the start of the list drawScreen. */
    public static void onDraw(IScrollList list, int mouseY) {
        Minecraft mc = Minecraft.getMinecraft();
        long now = Minecraft.getSystemTime();
        if (mc.currentScreen != screen) {
            LISTS.clear();
            screen = mc.currentScreen;
        }
        State state = list.hodgepodge$getState();
        long frameMs = now - state.lastDraw;
        state.lastDraw = now;
        if (!LISTS.contains(list)) {
            LISTS.add(list);
        }
        LISTS.removeIf(l -> now - l.hodgepodge$getState().lastDraw > STALE_MS);
        drag(list, state, mouseY);
        ease(list, state, frameMs);
    }

    /** Moves the list towards the wheel target, so a 30px step is not a jump that aliases with the 32px background. */
    private static void ease(IScrollList list, State state, long frameMs) {
        if (!state.easing) return;
        float current = list.hodgepodge$getScroll();
        float diff = state.easeTarget - current;
        if (state.mode != Mode.NONE || Math.abs(diff) < 0.5f) {
            if (state.mode == Mode.NONE) list.hodgepodge$setScroll(state.easeTarget);
            state.easing = false;
            return;
        }
        float k = 1f - (float) Math.exp(-Math.min(frameMs, 100L) / WHEEL_EASE_MS);
        list.hodgepodge$setScroll(current + diff * k);
    }

    private static float clampScroll(IScrollList list, float scroll) {
        float old = list.hodgepodge$getScroll();
        list.hodgepodge$setScroll(scroll);
        float clamped = list.hodgepodge$getScroll();
        list.hodgepodge$setScroll(old);
        return clamped;
    }

    /** Called at the start of GuiScreen.handleMouseInput. */
    public static void onMouseEvent(GuiScreen gui) {
        if (gui != screen) return;
        if (Mouse.getEventButton() == 0 && !Mouse.getEventButtonState()) {
            for (IScrollList list : LISTS) {
                list.hodgepodge$getState().mode = Mode.NONE;
            }
        }
        int wheel = Mouse.getEventDWheel();
        if (wheel != 0) {
            int x = eventX(gui);
            int y = eventY(gui);
            for (IScrollList list : LISTS) {
                if (contains(list, x, y)) {
                    State state = list.hodgepodge$getState();
                    float base = state.easing ? state.easeTarget : list.hodgepodge$getScroll();
                    state.easeTarget = clampScroll(list, base - Math.copySign(WHEEL_STEP, wheel));
                    state.easing = true;
                    return;
                }
            }
        }
    }

    /** Called after GuiScreen.mouseClicked, so a child entry that consumed the press can block the list. */
    public static void onMousePressed(GuiScreen gui) {
        if (gui != screen || Mouse.getEventButton() != 0) return;
        int x = eventX(gui);
        int y = eventY(gui);
        for (IScrollList list : LISTS) {
            if (list.hodgepodge$canClick() && contains(list, x, y)) {
                press(list, x, y);
            }
        }
    }

    private static void press(IScrollList list, int x, int y) {
        State state = list.hodgepodge$getState();
        state.mode = Mode.NONE;
        state.easing = false;
        if (x >= list.hodgepodge$getBarX() && x <= list.hodgepodge$getBarX() + BAR_WIDTH) {
            if (maxScroll(list) > 0) {
                int thumbHeight = thumbHeight(list);
                int thumbTop = thumbTop(list, thumbHeight);
                boolean onThumb = y >= thumbTop && y <= thumbTop + thumbHeight;
                state.grabOffset = onThumb ? y - thumbTop : thumbHeight / 2;
                state.mode = Mode.BAR;
                drag(list, state, y);
            }
        } else if (list.hodgepodge$clickElement(x, y)) {
            state.mode = Mode.CONTENT;
            state.anchorY = y;
            state.anchorScroll = list.hodgepodge$getScroll();
        }
    }

    private static void drag(IScrollList list, State state, int mouseY) {
        if (state.mode == Mode.BAR) {
            int max = maxScroll(list);
            int track = list.hodgepodge$getBottom() - list.hodgepodge$getTop() - thumbHeight(list);
            if (max > 0 && track > 0) {
                int thumbTop = mouseY - state.grabOffset - list.hodgepodge$getTop();
                list.hodgepodge$setScroll((float) thumbTop * max / track);
            }
        } else if (state.mode == Mode.CONTENT) {
            list.hodgepodge$setScroll(state.anchorScroll - (mouseY - state.anchorY));
        }
    }

    private static int maxScroll(IScrollList list) {
        return list.hodgepodge$getContentHeight() - (list.hodgepodge$getBottom() - list.hodgepodge$getTop() - 4);
    }

    private static int thumbHeight(IScrollList list) {
        int view = list.hodgepodge$getBottom() - list.hodgepodge$getTop();
        int height = view * view / list.hodgepodge$getContentHeight();
        return Math.min(Math.max(height, 32), view - 8);
    }

    private static int thumbTop(IScrollList list, int thumbHeight) {
        int view = list.hodgepodge$getBottom() - list.hodgepodge$getTop();
        return (int) list.hodgepodge$getScroll() * (view - thumbHeight) / maxScroll(list) + list.hodgepodge$getTop();
    }

    private static boolean contains(IScrollList list, int x, int y) {
        return x >= list.hodgepodge$getLeft() && x <= list.hodgepodge$getRight()
                && y >= list.hodgepodge$getTop()
                && y <= list.hodgepodge$getBottom();
    }

    private static int eventX(GuiScreen gui) {
        return Mouse.getEventX() * gui.width / Minecraft.getMinecraft().displayWidth;
    }

    private static int eventY(GuiScreen gui) {
        return gui.height - Mouse.getEventY() * gui.height / Minecraft.getMinecraft().displayHeight - 1;
    }
}
