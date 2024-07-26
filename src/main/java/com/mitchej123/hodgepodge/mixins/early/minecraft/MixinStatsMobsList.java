package com.mitchej123.hodgepodge.mixins.early.minecraft;

import java.util.Comparator;
import java.util.List;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.StatCollector;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mitchej123.hodgepodge.config.TweaksConfig;
import com.mitchej123.hodgepodge.util.StatHandler;
import com.mitchej123.hodgepodge.util.StatHandler.EntityInfo;

import cpw.mods.fml.client.FMLClientHandler;

@Mixin(targets = "net.minecraft.client.gui.achievement.GuiStats$StatsMobsList")
public class MixinStatsMobsList {

    // This List contains all instances of EntityEggInfo for which the stats should be displayed. The order in which
    // they are displayed is determined by the List's order
    @Shadow
    private @Final List<EntityEggInfo> field_148222_l;

    @Inject(at = @At("TAIL"), method = "<init>")
    private void hodgepodge$addModdedEntities(CallbackInfo ci) {
        StatFileWriter stats = FMLClientHandler.instance().getClientPlayerEntity().getStatFileWriter();
        for (EntityEggInfo info : StatHandler.ADDITIONAL_ENTITY_EGGS.values()) {
            // Is either the killed Entity or killed by Entity stat non-zero?
            // NOTE: StatFileWriter.writeStat() actually reads the stat (writing is done with
            // StatFileWriter.func_150873_a())
            if (stats.writeStat(info.field_151512_d) > 0 || stats.writeStat(info.field_151513_e) > 0) {
                this.field_148222_l.add(info);
            }
        }
        if (TweaksConfig.sortEntityStats) {
            this.field_148222_l.sort(new Comparator<>() {

                @Override
                public int compare(EntityEggInfo o1, EntityEggInfo o2) {
                    if (o1 == null) {
                        if (o2 == null) {
                            return 0;
                        }
                        return -1;
                    }
                    if (o2 == null) {
                        return 1;
                    }
                    String name1 = "entity." + getName(o1) + ".name";
                    String name2 = "entity." + getName(o2) + ".name";
                    return StatCollector.translateToLocal(name1)
                            .compareToIgnoreCase(StatCollector.translateToLocal(name2));
                }

                private static String getName(EntityEggInfo eei) {
                    if (eei instanceof EntityInfo info) {
                        return info.name;
                    } else {
                        return EntityList.getStringFromID(eei.spawnedID);
                    }
                }
            });
        }
    }

    @ModifyExpressionValue(
            at = @At(
                    target = "Lnet/minecraft/entity/EntityList;getStringFromID(I)Ljava/lang/String;",
                    value = "INVOKE"),
            method = "drawSlot")
    private static String hodgepodge$getEntityName(String original, @Local EntityEggInfo entityegginfo) {
        if (entityegginfo instanceof EntityInfo info) {
            return info.name;
        }
        return original;
    }
}
