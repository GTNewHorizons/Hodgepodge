package com.mitchej123.hodgepodge.mixins.callback;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

public final class CallbackInfoReturnableI extends CallbackInfo {

    private int returnValue;

    public CallbackInfoReturnableI(String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = 0;
    }

    public CallbackInfoReturnableI(String name, boolean cancellable, int returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public void setReturnValue(int returnValue) throws CancellationException {
        super.cancel();
        this.returnValue = returnValue;
    }

    public int getReturnValueI() {
        return this.returnValue;
    }
}
