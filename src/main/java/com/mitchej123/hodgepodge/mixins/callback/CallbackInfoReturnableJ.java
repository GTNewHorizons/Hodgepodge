package com.mitchej123.hodgepodge.mixins.callback;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

public final class CallbackInfoReturnableJ extends CallbackInfo {

    private long returnValue;

    public CallbackInfoReturnableJ(String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = 0L;
    }

    public CallbackInfoReturnableJ(String name, boolean cancellable, long returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public void setReturnValue(long returnValue) throws CancellationException {
        super.cancel();
        this.returnValue = returnValue;
    }

    public long getReturnValueJ() {
        return this.returnValue;
    }
}
