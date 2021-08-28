package com.mitchej123.hodgepodge.mixins.preventPickupLoot;

import net.minecraft.entity.EntityLiving;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityLiving.class)
public class MixinEntityLiving {
    @Shadow
    private boolean canPickUpLoot;
    
    /**
     * @author mitchej123
     * //@reason Stop monsters from picking up loot and not despawning
     */
    @Overwrite()
    public void setCanPickUpLoot(boolean canPickUpLoot) {
        this.canPickUpLoot = false;
    }
    
    /**
     * @author mitchej123
     * //@reason Stop monsters from picking up loot and not despawning
     */
    @Overwrite
    public boolean canPickUpLoot() {
        return false;
    }
}
