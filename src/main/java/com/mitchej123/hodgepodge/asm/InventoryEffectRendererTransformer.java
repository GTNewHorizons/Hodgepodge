package com.mitchej123.hodgepodge.asm;

import com.mitchej123.hodgepodge.asm.util.AbstractClassTransformer;
import com.mitchej123.hodgepodge.asm.util.AbstractMethodTransformer;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class InventoryEffectRendererTransformer extends AbstractClassTransformer {
    public InventoryEffectRendererTransformer()
    {
        if (!FMLLaunchHandler.side().name().equals("SERVER")) {
            methodTransformers.put(References.cEffectRederer.getMethod("drawScreen"), new AbstractMethodTransformer() {
                @Override
                public void transform() {
                    // Cut out super.drawScreen() sequence
                    AbstractInsnNode start = findNext(currentMethod.instructions.getFirst(), matchOpcode(Opcodes.ALOAD));
                    AbstractInsnNode[] instToMove = new AbstractInsnNode[5];
                    for (int i = 0; i < 5; ++i) {
                        instToMove[i] = start;
                        start = start.getNext();
                    }
                    for (int i = 0; i < 5; ++i)
                        currentMethod.instructions.remove(instToMove[i]);

                    // And paste it 2 instructions below status effect rendering (after the label)
                    InsnList listMove = new InsnList();
                    for (AbstractInsnNode n : instToMove)
                        listMove.add(n);
                    AbstractInsnNode inv = findNext(currentMethod.instructions.getFirst(), matchOpcode(Opcodes.INVOKESPECIAL));
                        log.info("Reordering InventoryEffectRenderer.drawScreen");
                        currentMethod.instructions.insert(inv.getNext(), listMove);
                }
            });
        }
    }
}
