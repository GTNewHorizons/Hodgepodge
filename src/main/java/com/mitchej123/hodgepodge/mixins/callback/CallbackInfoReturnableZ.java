package com.mitchej123.hodgepodge.mixins.callback;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

public final class CallbackInfoReturnableZ extends CallbackInfo {

    private boolean returnValue;

    public CallbackInfoReturnableZ(String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = false;
    }

    public CallbackInfoReturnableZ(String name, boolean cancellable, boolean returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public void setReturnValue(boolean returnValue) throws CancellationException {
        super.cancel();
        this.returnValue = returnValue;
    }

    public boolean getReturnValueZ() {
        return this.returnValue;
    }
}
