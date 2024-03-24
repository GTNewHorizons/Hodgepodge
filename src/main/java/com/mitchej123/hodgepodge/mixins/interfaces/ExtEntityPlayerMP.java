package com.mitchej123.hodgepodge.mixins.interfaces;

public interface ExtEntityPlayerMP {

    void setThrottled(boolean val);

    void setWasThrottled(boolean val);

    boolean isThrottled();

    boolean wasThrottled();
}
