package com.mitchej123.hodgepodge.loader;

import com.mitchej123.hodgepodge.loader.util.AbstractClassTransformer;
import com.mitchej123.hodgepodge.loader.util.AbstractMethodTransformer;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.VarInsnNode;

public class HodgePodgeClassTransformer extends AbstractClassTransformer {

    public HodgePodgeClassTransformer ()
    {
        if (!FMLLaunchHandler.side().name().equals("SERVER")) {
            /*
             * Grabs various methods in RenderBlocks when they query for colorMultiplier and overrides the value.
             */
            methodTransformers.put(References.cRenderBlocks.getMethod("renderStandardBlock"), new AbstractMethodTransformer() {
                @Override
                public void transform() {
                    log.info("Injecting RenderBlocks.renderStandardBlock");
                    insertBefore(matchVarInsn(Opcodes.ISTORE, 5),
                            new VarInsnNode(Opcodes.ALOAD, 1), //add block
                            new VarInsnNode(Opcodes.ILOAD, 2), //add x
                            new VarInsnNode(Opcodes.ILOAD, 4), //add z
                            createInvokeStatic(References.cHogClient.getMethod("renderStandardBlock_colorMultiplier")));
                }
            });
            methodTransformers.put(References.cRenderBlocks.getMethod("renderBlockLiquid"), new AbstractMethodTransformer() {
                @Override
                public void transform() {
                    log.info("Injecting RenderBlocks.renderBlockLiquid");
                    insertBefore(matchVarInsn(Opcodes.ISTORE, 6),
                            new VarInsnNode(Opcodes.ALOAD, 1), //add block
                            new VarInsnNode(Opcodes.ILOAD, 2), //add x
                            new VarInsnNode(Opcodes.ILOAD, 4), //add z
                            createInvokeStatic(References.cHogClient.getMethod("renderBlockLiquid_colorMultiplier")));
                }
            });
            methodTransformers.put(References.cRenderBlocks.getMethod("renderBlockDoublePlant"), new AbstractMethodTransformer() {
                @Override
                public void transform() {
                    log.info("Injecting RenderBlocks.renderBlockDoublePlant");
                    insertBefore(matchVarInsn(Opcodes.ISTORE, 6),
                            new VarInsnNode(Opcodes.ALOAD, 1), //add BlockDoublePlant
                            createCheckCast(References.cBlock), //cast it
                            new VarInsnNode(Opcodes.ILOAD, 2), //add x
                            new VarInsnNode(Opcodes.ILOAD, 4), //add z
                            createInvokeStatic(References.cHogClient.getMethod("renderBlockDoublePlant_colorMultiplier")));
                }
            });
            methodTransformers.put(References.cRenderBlocks.getMethod("renderCrossedSquares"), new AbstractMethodTransformer() {
                @Override
                public void transform() {
                    log.info("Injecting RenderBlocks.renderCrossedSquares");
                    insertBefore(matchVarInsn(Opcodes.ISTORE, 6),
                            new VarInsnNode(Opcodes.ALOAD, 1), //add block
                            new VarInsnNode(Opcodes.ILOAD, 2), //add x
                            new VarInsnNode(Opcodes.ILOAD, 4), //add z
                            createInvokeStatic(References.cHogClient.getMethod("renderCrossedSquares_colorMultiplier")));
                }
            });
            methodTransformers.put(References.cRenderBlocks.getMethod("renderBlockVine"), new AbstractMethodTransformer() {
                @Override
                public void transform() {
                    log.info("Injecting RenderBlocks.renderBlockVine");
                    insertBefore(matchVarInsn(Opcodes.ISTORE, 7),
                            new VarInsnNode(Opcodes.ALOAD, 1), //add block
                            new VarInsnNode(Opcodes.ILOAD, 2), //add x
                            new VarInsnNode(Opcodes.ILOAD, 4), //add z
                            createInvokeStatic(References.cHogClient.getMethod("renderBlockVine_colorMultiplier")));
                }
            });

            /*
             * Grabs various methods in biomesoplenty.client.render.blocks.FoliageRenderer when they query for colorMultiplier and overrides the value.
             */
            methodTransformers.put(References.bopFoliageRenderer.getMethod("renderCrossedSquares"), new AbstractMethodTransformer() {
                @Override
                public void transform() {
                    log.info("Injecting biomesoplenty.client.render.blocks.FoliageRenderer.");
                    insertBefore(matchVarInsn(Opcodes.ISTORE, 9),
                            new VarInsnNode(Opcodes.ALOAD, 1), //add block
                            new VarInsnNode(Opcodes.ILOAD, 2), //add x
                            new VarInsnNode(Opcodes.ILOAD, 4), //add z
                            createInvokeStatic(References.cHogClient.getMethod("renderCrossedSquares_colorMultiplier")));
                }
            });

        }
    }
}
