package com.mitchej123.hodgepodge;

import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhlib.mixin.IMixins;
import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import com.mitchej123.hodgepodge.mixins.Mixins;

@LateMixin
public class HodgepodgeLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.hodgepodge.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        return IMixins.getLateMixins(Mixins.class, loadedMods);
    }
}
