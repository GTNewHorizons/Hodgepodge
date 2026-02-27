package com.mitchej123.hodgepodge.mixins.callback;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

public final class CallbackInfoReturnableD extends CallbackInfo {

    private double returnValue;

    public CallbackInfoReturnableD(String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = 0.0d;
    }

    public CallbackInfoReturnableD(String name, boolean cancellable, double returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public void setReturnValue(double returnValue) throws CancellationException {
        super.cancel();
        this.returnValue = returnValue;
    }

    public double getReturnValueD() {
        return this.returnValue;
    }
}
