package com.mitchej123.hodgepodge.asm;

import com.mitchej123.hodgepodge.asm.util.AbstractClassTransformer;
import com.mitchej123.hodgepodge.asm.util.AbstractMethodTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public class InventoryEffectRendererTransformer extends AbstractClassTransformer {
    public InventoryEffectRendererTransformer() {
        addMethodTransformer(
                References.cEffectRenderer.getMethod("drawScreen"),
                ClassWriter.COMPUTE_FRAMES,
                new AbstractMethodTransformer() {
                    @Override
                    public void transform() {
                        // Cut out super.drawScreen() sequence
                        AbstractInsnNode start =
                                findNext(currentMethod.instructions.getFirst(), matchOpcode(Opcodes.ALOAD));
                        // this cannot be InsnList, because it would prematurely take ownership of the removed
                        // instructions
                        AbstractInsnNode[] instToMove = new AbstractInsnNode[5];
                        for (int i = 0; i < 5; ++i) {
                            instToMove[i] = start;
                            start = start.getNext();
                        }
                        for (int i = 0; i < 5; ++i) currentMethod.instructions.remove(instToMove[i]);

                        // And paste it 2 instructions below status effect rendering (after the label)
                        InsnList listMove = new InsnList();
                        for (AbstractInsnNode n : instToMove) listMove.add(n);
                        AbstractInsnNode inv =
                                findNext(currentMethod.instructions.getFirst(), matchOpcode(Opcodes.INVOKESPECIAL));
                        log.info("Reordering InventoryEffectRenderer.drawScreen");
                        currentMethod.instructions.insert(inv.getNext(), listMove);
                    }
                });
    }
}
