package com.mitchej123.hodgepodge.mixins.ic2;

import ic2.api.Direction;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import net.minecraft.tileentity.TileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TileEntityReactorChamberElectric.class)
public class MixinTileEntityReactorChamberElectricInvSpeedup {
    private TileEntityNuclearReactorElectric master;

    @Inject(method = "getReactor", remap = false, cancellable = true, at = @At(value = "HEAD"))
    public void onGetReactor(CallbackInfoReturnable<TileEntityNuclearReactorElectric> info) {
        if (master != null) {
            info.setReturnValue(master);
            info.cancel();
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
    public TileEntity onGotReactor(Direction thiz, TileEntity te) {
        TileEntity o = thiz.applyToTileEntity(te);
        if (o instanceof TileEntityNuclearReactorElectric) {
            master = (TileEntityNuclearReactorElectric) o;
            return o;
        }
        return null;
    }
}
