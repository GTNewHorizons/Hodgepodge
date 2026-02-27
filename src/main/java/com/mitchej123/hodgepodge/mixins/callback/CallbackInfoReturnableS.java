package com.mitchej123.hodgepodge.mixins.callback;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

public final class CallbackInfoReturnableS extends CallbackInfo {

    private short returnValue;

    public CallbackInfoReturnableS(String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = 0;
    }

    public CallbackInfoReturnableS(String name, boolean cancellable, short returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public void setReturnValue(short returnValue) throws CancellationException {
        super.cancel();
        this.returnValue = returnValue;
    }

    public short getReturnValueS() {
        return this.returnValue;
    }
}
