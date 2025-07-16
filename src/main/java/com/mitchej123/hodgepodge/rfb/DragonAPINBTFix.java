package com.mitchej123.hodgepodge.rfb;

import com.gtnewhorizons.retrofuturabootstrap.api.ClassNodeHandle;
import com.gtnewhorizons.retrofuturabootstrap.api.ExtensibleClassLoader;
import com.gtnewhorizons.retrofuturabootstrap.api.RfbClassTransformer;
import java.nio.charset.StandardCharsets;
import java.util.jar.Manifest;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * Used with permission under the MIT license. See 3RD-PARTY-LICENCES for details
 * @author FalsePattern
 */
public class DragonAPINBTFix implements RfbClassTransformer {
    private static final byte[] NAME_BYTES = "Reika.DragonAPI.Instantiable.Data.Maps.MultiMap".getBytes(StandardCharsets.UTF_8);
    private static final String DESC_OG = "(Lnet/minecraft/nbt/NBTTagList;LReika/DragonAPI/Libraries/ReikaNBTHelper$NBTIO;LReika/DragonAPI/Libraries/ReikaNBTHelper$NBTIO;)V";
    private static final String HOOK_CLASS = "com.example.ASMHooks".replace('.', '/'); // TODO change this
    private static final String DESC_HOOK = "(LReika/DragonAPI/Instantiable/Data/Maps/MultiMap;Lnet/minecraft/nbt/NBTTagList;LReika/DragonAPI/Libraries/ReikaNBTHelper$NBTIO;LReika/DragonAPI/Libraries/ReikaNBTHelper$NBTIO;)V";
    @Pattern("[a-z0-9-]+")
    @Override
    public @NotNull String id() {
        return "dragonapi-nbtfix";
    }

    @Override
    public boolean shouldTransformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context, @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        if ("Reika.DragonAPI.Instantiable.Data.Maps.MultiMap".equals(className))
            return true;

        if (classNode.isOriginal()) {
            var bytes = classNode.getOriginalBytes();
            if (bytes == null) {
                return false;
            }
            for (int i = 0; i < bytes.length - NAME_BYTES.length; i++) {
                if (equals(bytes, i, i + NAME_BYTES.length, NAME_BYTES, 0, NAME_BYTES.length)) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void transformClass(@NotNull ExtensibleClassLoader classLoader, @NotNull Context context, @Nullable Manifest manifest, @NotNull String className, @NotNull ClassNodeHandle classNode) {
        var cn = classNode.getNode();
        if (cn == null) {
            return;
        }
        if ("Reika.DragonAPI.Instantiable.Data.Maps.MultiMap".equals(className)) {
            for (var field: cn.fields) {
                if (field.name.equals("data")) {
                    field.access &= ~Opcodes.ACC_PRIVATE;
                    field.access |= Opcodes.ACC_PUBLIC;
                    break;
                }
            }
            var mIter = cn.methods.listIterator();
            while (mIter.hasNext()) {
                var method = mIter.next();
                switch (method.name) {
                    case "writeToNBT", "readFromNBT" -> mIter.remove();
                    case "createCollection" -> {
                        method.access &= ~Opcodes.ACC_PRIVATE;
                        method.access |= Opcodes.ACC_PUBLIC;
                    }
                }
            }
        }

        for (var method: cn.methods) {
            for (var insn : method.instructions.toArray()) {
                if (!(insn instanceof MethodInsnNode mInsn)) {
                    continue;
                }
                switch (mInsn.name) {
                    case "writeToNBT", "readFromNBT" -> {
                        if (!mInsn.owner.equals("Reika/DragonAPI/Instantiable/Data/Maps/MultiMap"))
                            continue;
                        if (!mInsn.desc.equals(DESC_OG))
                            continue;
                        mInsn.setOpcode(Opcodes.INVOKESTATIC);
                        mInsn.owner = HOOK_CLASS;
                        mInsn.desc = DESC_HOOK;
                    }
                }
            }
        }
    }

    /**
     * Stub because Java 8 doesn't have this
     */
    public static boolean equals(
            byte[] a, int aFromIndex, int aToIndex,
            byte[] b, int bFromIndex, int bToIndex
    ) {
        int aLength = aToIndex - aFromIndex;
        int bLength = bToIndex - bFromIndex;
        if (aLength != bLength) {
            return false;
        }
        if (aLength == 0) {
            return true;
        }
        for (int i = 0; i < aLength; i++) {
            if (a[aFromIndex + i] != b[bFromIndex + i]) {
                return false;
            }
        }
        return true;
    }
}