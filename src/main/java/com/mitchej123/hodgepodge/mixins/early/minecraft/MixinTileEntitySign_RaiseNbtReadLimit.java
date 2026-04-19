package com.mitchej123.hodgepodge.mixins.early.minecraft;

import net.minecraft.tileentity.TileEntitySign;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * Vanilla {@code readFromNBT} truncates each sign line to 15 raw chars. With {@code &} color codes, a legitimate line
 * is longer than 15 raw chars (e.g. {@code &g&#FF0000&#0000FFhello} is 23 raw but 5 visible). Raise the cap to 90 to
 * match the server-side safety cap in {@link MixinNetHandlerPlayServer_SignLimit}. Both the {@code length() > 15} check
 * and the {@code substring(0, 15)} call use the same constant, so a single ModifyConstant covers both.
 */
@Mixin(TileEntitySign.class)
public class MixinTileEntitySign_RaiseNbtReadLimit {

    @ModifyConstant(method = "readFromNBT", constant = @Constant(intValue = 15))
    private int hodgepodge$raiseNbtReadLimit(int original) {
        return 90;
    }
}
