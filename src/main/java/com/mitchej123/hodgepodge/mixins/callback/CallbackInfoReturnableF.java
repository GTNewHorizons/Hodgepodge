package com.mitchej123.hodgepodge.mixins.callback;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

public final class CallbackInfoReturnableF extends CallbackInfo {

    private float returnValue;

    public CallbackInfoReturnableF(String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = 0.0f;
    }

    public CallbackInfoReturnableF(String name, boolean cancellable, float returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public void setReturnValue(float returnValue) throws CancellationException {
        super.cancel();
        this.returnValue = returnValue;
    }

    public float getReturnValueF() {
        return this.returnValue;
    }
}
