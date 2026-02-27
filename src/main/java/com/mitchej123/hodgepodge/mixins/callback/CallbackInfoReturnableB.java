package com.mitchej123.hodgepodge.mixins.callback;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

public final class CallbackInfoReturnableB extends CallbackInfo {

    private byte returnValue;

    public CallbackInfoReturnableB(String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = 0;
    }

    public CallbackInfoReturnableB(String name, boolean cancellable, byte returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public void setReturnValue(byte returnValue) throws CancellationException {
        super.cancel();
        this.returnValue = returnValue;
    }

    public byte getReturnValueB() {
        return this.returnValue;
    }
}
