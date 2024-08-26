package com.mitchej123.hodgepodge.mixins.hooks;

import cpw.mods.fml.common.ModContainer;

public class ModProfileResult implements Comparable<ModProfileResult> {

    public final long time;
    public final ModContainer mod;

    public ModProfileResult(long time, ModContainer mod) {
        this.time = time;
        this.mod = mod;
    }

    @Override
    public int compareTo(ModProfileResult o) {
        return Long.compare(o.time, time);
    }

}
