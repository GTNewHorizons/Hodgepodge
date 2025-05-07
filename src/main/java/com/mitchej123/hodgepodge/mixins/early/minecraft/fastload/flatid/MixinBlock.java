package com.mitchej123.hodgepodge.mixins.early.minecraft.fastload.flatid;

import net.minecraft.block.Block;
import net.minecraft.util.RegistryNamespaced;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mitchej123.hodgepodge.mixins.interfaces.BlockExt_ID;

@Mixin(Block.class)
public class MixinBlock implements BlockExt_ID {

    @Unique
    private int hodgepodge$id = -1;

    @Override
    public int hodgepodge$getID() {
        return hodgepodge$id;
    }

    @Override
    public void hodgepodge$setID(int id) {
        hodgepodge$id = id;
    }

    @Redirect(
            method = "getIdFromBlock",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RegistryNamespaced;getIDForObject(Ljava/lang/Object;)I"))
    private static int hodgepodge$getID(RegistryNamespaced instance, Object object) {
        if (!(object instanceof BlockExt_ID block)) return -1;
        return block.hodgepodge$getID();
    }
}
