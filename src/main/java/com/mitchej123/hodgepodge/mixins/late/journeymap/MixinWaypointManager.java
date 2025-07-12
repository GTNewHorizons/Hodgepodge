package com.mitchej123.hodgepodge.mixins.late.journeymap;

import net.minecraft.util.MathHelper;

import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import cpw.mods.fml.common.Loader;
import journeymap.client.ui.component.JmUI;
import journeymap.client.ui.component.ScrollListPane;
import journeymap.client.ui.waypoint.WaypointManager;

@Mixin(WaypointManager.class)
public abstract class MixinWaypointManager extends JmUI {

    @Shadow(remap = false)
    protected ScrollListPane<?> itemScrollPane;

    @Shadow(remap = false)
    protected int rowHeight;

    @Unique
    private final boolean hasLwjgl3 = Loader.isModLoaded("lwjgl3ify");

    /**
     * @author eigenraven
     * @reason Reversed clamping (was: {@code if(delta > 1) delta = -1;} and vice versa)
     */
    @Overwrite(remap = false)
    public void handleMouseInput() {
        super.handleMouseInput();
        int delta = Mouse.getEventDWheel();
        if (delta != 0) {
            if (!hasLwjgl3) {
                // LWJGL 2's reported scroll amounts are not uniformly scaled across operating systems
                delta = MathHelper.clamp_int(delta, -1, 1);
            }
            this.itemScrollPane.scrollBy(-delta * this.rowHeight);
        }
    }

    /* Forced to have constructor matching super */
    private MixinWaypointManager(String title) {
        super(title);
    }
}
