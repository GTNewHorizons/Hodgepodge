package com.mitchej123.hodgepodge.mixins.callback;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CancellationException;

public final class CallbackInfoReturnableC extends CallbackInfo {

    private char returnValue;

    public CallbackInfoReturnableC(String name, boolean cancellable) {
        super(name, cancellable);
        this.returnValue = '\0';
    }

    public CallbackInfoReturnableC(String name, boolean cancellable, char returnValue) {
        super(name, cancellable);
        this.returnValue = returnValue;
    }

    public void setReturnValue(char returnValue) throws CancellationException {
        super.cancel();
        this.returnValue = returnValue;
    }

    public char getReturnValueC() {
        return this.returnValue;
    }
}
