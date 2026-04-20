package com.mitchej123.hodgepodge.core;

import java.io.IOException;

import cpw.mods.fml.common.asm.transformers.AccessTransformer;

public class CoFHCoreAccessTransformer extends AccessTransformer {

    public CoFHCoreAccessTransformer() throws IOException {
        super("META-INF/cofh_at.cfg");
    }
}
