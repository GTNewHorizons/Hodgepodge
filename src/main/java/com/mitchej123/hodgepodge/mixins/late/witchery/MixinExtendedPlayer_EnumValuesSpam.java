package com.mitchej123.hodgepodge.mixins.late.witchery;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import com.emoniph.witchery.common.ExtendedPlayer;
import com.emoniph.witchery.util.TransformCreature;

@Mixin(ExtendedPlayer.class)
public class MixinExtendedPlayer_EnumValuesSpam {

    @Unique
    private static final TransformCreature[] hodgepodge$VALUES = TransformCreature.values();

    @Shadow
    private int creatureType;

    /**
     * @author Alexdoru
     * @reason prevent allocation spam
     */
    @Overwrite(remap = false)
    public TransformCreature getCreatureType() {
        return hodgepodge$VALUES[this.creatureType];
    }
}
