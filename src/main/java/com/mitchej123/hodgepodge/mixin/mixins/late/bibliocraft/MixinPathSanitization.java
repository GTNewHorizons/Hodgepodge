package com.mitchej123.hodgepodge.mixin.mixins.late.bibliocraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.mitchej123.hodgepodge.util.PathSanitizer;

import jds.bibliocraft.FileUtil;

@Mixin(FileUtil.class)
public class MixinPathSanitization {

    @ModifyArg(
            method = { "isBookSaved", "saveBook", "loadBook", "getAuthorList", "getPublistList",
                    "addPublicPrivateFieldToBook", "deleteBook", "updatePublicFlag", "saveNBTtoFile", "saveBookMeta",
                    "getBookType(Lnet/minecraft/world/World;I)I", "getBookType(ZI)I", "loadBookNBT" },
            at = @At(value = "INVOKE", target = "Ljava/io/File;<init>(Ljava/io/File;Ljava/lang/String;)V"),
            index = 1,
            remap = false)
    private String hodgepodge$sanitizeBookPath(String v) {
        return PathSanitizer.sanitizeFileName(v);
    }
}
