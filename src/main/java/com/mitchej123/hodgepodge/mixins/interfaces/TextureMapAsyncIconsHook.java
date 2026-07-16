package com.mitchej123.hodgepodge.mixins.interfaces;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import net.minecraft.util.IIcon;

public interface TextureMapAsyncIconsHook {

    Map<String, CompletableFuture<IIcon>> hodgepodge$getProcessingIcons();
}
