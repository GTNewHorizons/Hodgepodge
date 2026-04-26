package com.mitchej123.hodgepodge.mixins.late.bibliocraft.leaks;

import jds.bibliocraft.rendering.TileEntityArmorStandRenderer;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntityArmorStandRenderer.class)
public abstract class MixinTileEntityArmorStandRenderer extends TileEntitySpecialRenderer {

    @Shadow(remap = false)
    private AbstractClientPlayer steve;

    @Override
    public void func_147496_a(World world) {
        this.steve = null;
    }
}
