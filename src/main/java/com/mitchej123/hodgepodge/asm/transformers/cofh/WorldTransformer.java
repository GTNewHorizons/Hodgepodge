package com.mitchej123.hodgepodge.asm.transformers.cofh;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.POP;

import java.util.ListIterator;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class WorldTransformer implements IClassTransformer {
    /**
     *    Copyright 2020 Glease (https://github.com/Glease/)
     *
     *    Licensed under the Apache License, Version 2.0 (the "License");
     *    you may not use this file except in compliance with the License.
     *    You may obtain a copy of the License at
     *
     *        http://www.apache.org/licenses/LICENSE-2.0
     *
     *    Unless required by applicable law or agreed to in writing, software
     *    distributed under the License is distributed on an "AS IS" BASIS,
     *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
     *    See the License for the specific language governing permissions and
     *    limitations under the License.
     *
     *   Taken from from https://github.com/Glease/CoFHCoreFix
     *     * Removes cofh_recentTiles that is never used/cleaned up
     */
    private static final Logger LOGGER = LogManager.getLogger("CoFHCoreFix");

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if ("net.minecraft.world.World".equals(transformedName)) {
            LOGGER.info("Patching net.minecraft.world.World");
            ClassReader cr = new ClassReader(basicClass);
            ClassNode cn = new ClassNode(ASM5);
            cr.accept(cn, ClassReader.EXPAND_FRAMES);

            int pushReplaced = 0;

            for (MethodNode m : cn.methods) {
                for (ListIterator<AbstractInsnNode> it = m.instructions.iterator(); it.hasNext(); ) {
                    AbstractInsnNode node = it.next();
                    if (node instanceof MethodInsnNode) {
                        MethodInsnNode n = (MethodInsnNode) node;
                        if ("cofh/lib/util/LinkedHashList".equals(n.owner)
                                && "push".equals(n.name)
                                && "(Ljava/lang/Object;)Z".equals(n.desc)) {
                            LOGGER.debug("Replacing LinkedHashList::push inside {}{}", n.name, n.desc);
                            m.instructions.insertBefore(n, new InsnNode(POP));
                            m.instructions.remove(n);
                            pushReplaced++;
                            break;
                        }
                    }
                }
            }
            LOGGER.info("Removed {} occurrence of LinkedHashList::push", pushReplaced);
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            cn.accept(cw);
            return cw.toByteArray();
        } else {
            return basicClass;
        }
    }
}
