/**
 * Taken from  https://github.com/makamys/CoreTweaks under the MIT license
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2021 makamys
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.mitchej123.hodgepodge.mixins.early.minecraft;

import io.netty.channel.ChannelInitializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@SuppressWarnings("rawtypes")
@Mixin(
        targets = {
            "net.minecraft.network.NetworkSystem$1",
            "net.minecraft.network.NetworkManager$2",
            "net.minecraft.client.network.OldServerPinger$2"
        },
        remap = false)
public abstract class MixinTcpNoDelay extends ChannelInitializer {

    @ModifyArg(
            method = "initChannel",
            at = @At(value = "INVOKE", target = "Ljava/lang/Boolean;valueOf(Z)Ljava/lang/Boolean;"))
    private boolean hodgepodge$setTCPNoDelay(boolean original) {
        return true;
    }
}
