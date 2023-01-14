package com.mitchej123.hodgepodge.mixins.early.ic2;

import ic2.api.Direction;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import java.lang.ref.WeakReference;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityReactorChamberElectric.class)
public class MixinTileEntityReactorChamberElectricInvSpeedup {

    @Unique
    private WeakReference<TileEntityNuclearReactorElectric> master;

    @Inject(method = "getReactor", remap = false, cancellable = true, at = @At(value = "HEAD"))
    public void hodgepodge$onGetReactor(CallbackInfoReturnable<TileEntityNuclearReactorElectric> info) {
        if (master != null) {
            TileEntityNuclearReactorElectric ret;
            if ((ret = master.get()) != null && !ret.isInvalid()) {
                info.setReturnValue(ret);
                info.cancel();
            } else {
                master = null;
            }
        }
    }

    @Redirect(
            method = "getReactor",
            remap = false,
            at =
                    @At(
                            value = "INVOKE",
                            target =
                                    "Lic2/api/Direction;applyToTileEntity(Lnet/minecraft/tileentity/TileEntity;)Lnet/minecraft/tileentity/TileEntity;"))
    public TileEntity hodgepodge$onGotReactor(Direction thiz, TileEntity te) {
        TileEntity o = thiz.applyToTileEntity(te);
        if (o instanceof TileEntityNuclearReactorElectric) {
            master = new WeakReference<>((TileEntityNuclearReactorElectric) o);
            return o;
        }
        return null;
    }
}
