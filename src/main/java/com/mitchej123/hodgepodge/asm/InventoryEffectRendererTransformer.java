package com.mitchej123.hodgepodge.asm;

import com.mitchej123.hodgepodge.asm.util.AbstractClassTransformer;
import com.mitchej123.hodgepodge.asm.util.AbstractMethodTransformer;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class InventoryEffectRendererTransformer extends AbstractClassTransformer {
    public InventoryEffectRendererTransformer() {
        addMethodTransformer(
                References.cEffectRenderer.getMethod("drawScreen"),
                ClassWriter.COMPUTE_FRAMES,
                new AbstractMethodTransformer() {
                    @Override
                    public void transform() {

                        final String NEIClientConfigClasspath = "codechicken/nei/NEIClientConfig";
                        try {
                            if (this.getClass().getClassLoader().getResource(NEIClientConfigClasspath + ".class")
                                    == null) {
                                log.info("Skip reordering InventoryEffectRenderer.drawScreen since NEI is not loaded");
                                return;
                            }
                        } catch (Exception ignored) {
                        }

                        /*
                        ============ORIGINAL CODE=========

                         super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
                         if (this.field_147045_u) {
                             this.func_147044_g();
                         }

                         ============TARGET CODE==========

                         boolean bookmarkPanelHidden = NEIClientConfig.isHidden();
                         if (bookmarkPanelHidden) {
                             super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
                         }
                         if (this.field_147045_u) {
                             this.func_147044_g();
                         }
                         if (bookmarkPanelHidden) {
                             return;
                         }
                         super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);

                         ==========TARGET BYTECODE========

                         L0
                         INVOKESTATIC codechicken/nei/NEIClientConfig.isHidden ()Z
                         DUP
                         ISTORE 4
                         IFEQ L5
                         LINENUMBER 44 L0
                         ALOAD 0
                         ILOAD 1
                         ILOAD 2
                         FLOAD 3
                         INVOKESPECIAL net/minecraft/client/gui/inventory/GuiContainer.drawScreen (IIF)V
                        L5
                        L1
                         LINENUMBER 46 L1
                         ALOAD 0
                         GETFIELD net/minecraft/client/renderer/InventoryEffectRenderer.field_147045_u : Z
                         IFEQ L2
                        L3
                         LINENUMBER 48 L3
                         ALOAD 0
                         INVOKESPECIAL net/minecraft/client/renderer/InventoryEffectRenderer.func_147044_g ()V
                        L2
                         ILOAD 4
                         IFEQ L6
                         RETURN
                        L6
                         ALOAD 0
                         ILOAD 1
                         ILOAD 2
                         FLOAD 3
                         INVOKESPECIAL net/minecraft/client/gui/inventory/GuiContainer.drawScreen (IIF)V
                         LINENUMBER 50 L2
                        FRAME SAME
                         RETURN
                        L4
                         */

                        AbstractInsnNode startNodeSuperCall =
                                findNext(currentMethod.instructions.getFirst(), matchOpcode(Opcodes.ALOAD));
                        InsnList superCallInsnList = new InsnList();
                        for (int i = 0; i < 5; ++i) {
                            superCallInsnList.add(startNodeSuperCall.clone(null));
                            startNodeSuperCall = startNodeSuperCall.getNext();
                        }

                        int ordinal = 0;
                        AbstractInsnNode drawScreenMethodeInsnNode = null;
                        AbstractInsnNode drawPotionEffectsNode = null;
                        for (AbstractInsnNode insnNode : currentMethod.instructions.toArray()) {
                            if (insnNode instanceof MethodInsnNode && insnNode.getOpcode() == Opcodes.INVOKESPECIAL) {
                                if (ordinal == 0) {
                                    drawScreenMethodeInsnNode = insnNode;
                                    ordinal++;
                                    continue;
                                }
                                if (ordinal == 1) {
                                    drawPotionEffectsNode = insnNode;
                                }
                            }
                        }

                        if (drawScreenMethodeInsnNode == null || drawPotionEffectsNode == null) {
                            log.info("Failed reordering InventoryEffectRenderer.drawScreen");
                            return;
                        }

                        /*
                         * Injects before super call :
                         * boolean bookmarkPanelHidden = NEIClientConfig.isHidden();
                         * if (bookmarkPanelHidden) {
                         */
                        LabelNode skipFirstIfLabel = new LabelNode();
                        InsnList headInjectionList = new InsnList();
                        headInjectionList.add(new MethodInsnNode(
                                Opcodes.INVOKESTATIC, NEIClientConfigClasspath, "isHidden", "()Z", false));
                        headInjectionList.add(new InsnNode(Opcodes.DUP));
                        headInjectionList.add(new VarInsnNode(Opcodes.ISTORE, 4));
                        headInjectionList.add(new JumpInsnNode(Opcodes.IFEQ, skipFirstIfLabel));
                        currentMethod.instructions.insert(currentMethod.instructions.getFirst(), headInjectionList);

                        /*
                         * Injects end of first if after the drawScreen super call
                         */
                        currentMethod.instructions.insert(drawScreenMethodeInsnNode, skipFirstIfLabel);

                        /*
                         * Injects at the end of the method :
                         * if(bookmarkPanelHidden){
                         *      return;
                         * }
                         * super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
                         */
                        InsnList secondDrawScreenInsnList = new InsnList();
                        LabelNode skipSecondIfLabel = new LabelNode();
                        secondDrawScreenInsnList.add(new VarInsnNode(Opcodes.ILOAD, 4));
                        secondDrawScreenInsnList.add(new JumpInsnNode(Opcodes.IFEQ, skipSecondIfLabel));
                        secondDrawScreenInsnList.add(new InsnNode(Opcodes.RETURN));
                        secondDrawScreenInsnList.add(skipSecondIfLabel);
                        secondDrawScreenInsnList.add(superCallInsnList);
                        currentMethod.instructions.insert(drawPotionEffectsNode.getNext(), secondDrawScreenInsnList);

                        currentMethod.maxLocals = 5;
                        log.info("Reordering InventoryEffectRenderer.drawScreen");
                    }
                });
    }
}
