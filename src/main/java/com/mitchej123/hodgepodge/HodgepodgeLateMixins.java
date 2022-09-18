package com.mitchej123.hodgepodge;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;
import com.mitchej123.hodgepodge.mixins.Mixins;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@LateMixin
public class HodgepodgeLateMixins implements ILateMixinLoader {
    @Override
    public String getMixinConfig() {
        return "mixins.hodgepodge.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        final List<String> mixins = new ArrayList<>();
        final List<String> notLoading = new ArrayList<>();
        for (Mixins mixin : Mixins.values()) {
            if (mixin.phase == Mixins.Phase.LATE) {
                if (mixin.shouldLoad(Collections.emptySet(), loadedMods)) {
                    mixins.addAll(mixin.mixinClasses);
                } else {
                    notLoading.addAll(mixin.mixinClasses);
                }
            }
        }
        Common.log.info("Not loading the following LATE mixins: {}", notLoading.toString());
        return mixins;
    }
}
