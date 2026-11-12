package com.mitchej123.hodgepodge.client.scrolllist;

public interface IScrollList {

    int hodgepodge$getTop();

    int hodgepodge$getBottom();

    int hodgepodge$getLeft();

    int hodgepodge$getRight();

    int hodgepodge$getBarX();

    int hodgepodge$getContentHeight();

    float hodgepodge$getScroll();

    void hodgepodge$setScroll(float scroll);

    boolean hodgepodge$canClick();

    boolean hodgepodge$clickElement(int x, int y);

    ScrollListInput.State hodgepodge$getState();
}
