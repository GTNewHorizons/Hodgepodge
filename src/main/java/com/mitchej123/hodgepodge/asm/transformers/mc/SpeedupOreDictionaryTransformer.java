package com.mitchej123.hodgepodge.asm.transformers.mc;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import com.mitchej123.hodgepodge.Common;
import com.mitchej123.hodgepodge.core.HodgepodgeCore;

/**
 * Transformer that optimizes OreDictionary by replacing boxed Integer types with primitives and swapping standard Java
 * collections with fastutil equivalents that are optimized for primitive types.
 */
// Spotless keeps indenting each else-if to the right every time
// spotless:off
public class SpeedupOreDictionaryTransformer implements IClassTransformer, Opcodes {

    public static final String JAVA_HASH_SET = "java/util/HashSet";

    private static final Logger LOGGER = LogManager.getLogger("SpeedupOreDictionaryTransformer");
    //private static final Printer printer = new Textifier();
    //private static final TraceMethodVisitor mp = new TraceMethodVisitor(printer);

    private static final String ORE_DICTIONARY = "net/minecraftforge/oredict/OreDictionary";

    private static final String JAVA_HASH_MAP = "java/util/HashMap";
    private static final String JAVA_MAP = "java/util/Map";
    private static final String FASTUTIL_INT_OPEN_HASH_SET = "it/unimi/dsi/fastutil/ints/IntOpenHashSet";
    private static final String FASTUTIL_INT_COLLECTION = "it/unimi/dsi/fastutil/ints/IntCollection";
    private static final String FASTUTIL_OBJECT_2_INT_HASH_MAP = "it/unimi/dsi/fastutil/objects/Object2IntOpenHashMap";
    private static final String FASTUTIL_INT_2_OBJECT_HASH_MAP = "it/unimi/dsi/fastutil/ints/Int2ObjectOpenHashMap";
    private static final String INTEGER = "java/lang/Integer";

    private String itemStackClass;
    private String itemClass;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        if (transformedName.equals("net.minecraftforge.oredict.OreDictionary")) {
            boolean isObf = HodgepodgeCore.isObf();
            this.itemStackClass = isObf ? "add" : "net/minecraft/item/ItemStack";
            this.itemClass = isObf ? "adb" : "net/minecraft/item/Item";
            Common.logASM(LOGGER,"OreDictionary is obfuscated: {"+isObf+"}");
            return transformOreDictionary(basicClass);
        }

        return basicClass;
    }

    private byte[] transformOreDictionary(byte[] basicClass) {
        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(basicClass);
        classReader.accept(classNode, 0);

        boolean modified = false;

        // Add EMPTY_INT_ARRAY field
        FieldNode emptyIntArrayField = new FieldNode(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, "EMPTY_INT_ARRAY", "[I", null, null);
        classNode.fields.add(emptyIntArrayField);
        // Find and transform the methods
        Common.logASM(LOGGER,"Transforming OreDictionary class");
        for (MethodNode method : classNode.methods) {
            if ("<clinit>".equals(method.name)) {
                Common.logASM(LOGGER,"Transforming OreDictionary.<clinit>");
                modified |= transformClinitMethod(method);
            } else if ("getOreID".equals(method.name) && "(Ljava/lang/String;)I".equals(method.desc)) {
                Common.logASM(LOGGER,"Transforming OreDictionary.getOreID(String)");
                modified |= transformGetOreIDStringMethod(method);
            } else if ("getOreID".equals(method.name) && ("(L" + this.itemStackClass + ";)I").equals(method.desc)) {
                Common.logASM(LOGGER,"Transforming OreDictionary.getOreID(ItemStack)");
                modified |= transformGetOreIDItemStackMethod(method);
            } else if ("getOreIDs".equals(method.name) && ("(L" + this.itemStackClass + ";)[I").equals(method.desc)) {
                Common.logASM(LOGGER,"Transforming OreDictionary.getOreIDs(ItemStack)");
                modified |= transformGetOreIDsMethod(method);
            } else if ("getOres".equals(method.name) && "(Ljava/lang/String;Z)Ljava/util/List;".equals(method.desc)) {
                Common.logASM(LOGGER,"Transforming OreDictionary.getOres(String, boolean)");
                modified |= transformGetOresMethod(method);
            } else if ("getOres".equals(method.name) && "(I)Ljava/util/ArrayList;".equals(method.desc)) {
                Common.logASM(LOGGER,"Transforming OreDictionary.getOres(int)");
                modified |= transformGetOresIntMethod(method);
            } else if ("registerOreImpl".equals(method.name) && ("(Ljava/lang/String;L" + this.itemStackClass + ";)V").equals(method.desc)) {
                Common.logASM(LOGGER,"Transforming OreDictionary.registerOreImpl(String, ItemStack)");
                modified |= transformRegisterOreImplMethod(method);
            } else if ("rebakeMap".equals(method.name) && "()V".equals(method.desc)) {
                Common.logASM(LOGGER,"Transforming OreDictionary.rebakeMap()");
                modified |= transformRebakeMapMethod(method);
            }
            for(LocalVariableNode localVar : method.localVariables) {
                if ("Ljava/lang/Integer;".equals(localVar.desc)) {
                    localVar.desc = "I"; // "I" is the descriptor for int
                    // If signature is present, clear it since primitives don't have signatures
                    if (localVar.signature != null && localVar.signature.contains("Integer")) {
                        localVar.signature = null;
                    }
                }
            }
        }

        if (modified) {
            try {
                Common.logASM(LOGGER,"Writing transformed OreDictionary class");
                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                classNode.accept(writer);
                return writer.toByteArray();
            } catch (Exception e) {
                LOGGER.error("Failed to transform OreDictionary class", e);
                throw new RuntimeException(e);
            }
        }

        return basicClass;
    }

    private boolean transformClinitMethod(MethodNode method) {
        InsnList instructions = method.instructions;
        boolean modified = false;


        // Get an iterator to traverse through the instructions
        ListIterator<AbstractInsnNode> iterator = instructions.iterator();

        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();

            if (node.getOpcode() == PUTSTATIC && node instanceof FieldInsnNode fNode && fNode.owner.equals(ORE_DICTIONARY) && fNode.name.equals("idToName")) {
                // Replace HashMap with Object2IntOpenHashMap (for nameToId)
                // Rather than finding this first and looking back, we look for idToName which is declared immediately
                // before this, and then move forward

                modified = true;
    
                AbstractInsnNode next = node.getNext();
                while(next != null && next.getOpcode() != NEW) {
                    next = next.getNext();
                }

                if (next != null && next.getOpcode() == NEW && next instanceof TypeInsnNode tNode && tNode.desc.equals(JAVA_HASH_MAP)) {
                    tNode.desc = FASTUTIL_OBJECT_2_INT_HASH_MAP;
                } else {
                    throw new RuntimeException("Unexpected instruction found in OreDictionary.<clinit>");
                }
    
                // Look for INVOKESPECIAL HashMap.<init> and replace with Object2IntOpenHashMap.<init>
                while (next != null && next.getOpcode() != INVOKESPECIAL) {
                    next = next.getNext();
                }
                if (next != null && next.getOpcode() == INVOKESPECIAL && next instanceof MethodInsnNode mNext && mNext.owner.equals(JAVA_HASH_MAP)) {
                    mNext.owner = FASTUTIL_OBJECT_2_INT_HASH_MAP;
                } else {
                    throw new RuntimeException("Unexpected instruction found in OreDictionary.<clinit>");
                }
            } else if (node.getOpcode() == PUTSTATIC && node instanceof FieldInsnNode fNode && fNode.owner.equals(ORE_DICTIONARY)
                    && fNode.name.equals("idToStackUn"))
            {
                // Replace Maps.newHashMapWithExpectedSize with Int2ObjectOpenHashMap (for stackToId)
                // We do this by finding the PUTSTATIC idToStackUn which happens immediately before this
                modified = true;
    
                AbstractInsnNode next = node.getNext();
                // Create new instructions for Int2ObjectOpenHashMap initialization
                InsnList newInstructions = new InsnList();
                newInstructions.add(new TypeInsnNode(NEW, FASTUTIL_INT_2_OBJECT_HASH_MAP));
                newInstructions.add(new InsnNode(DUP));
                instructions.insert(node, newInstructions);
    
                // Look for INVOKESTATIC Maps.newHashMapWithExpectedSize
                while (next != null && next.getOpcode() != INVOKESTATIC) 
                    next = next.getNext();
    
                if (next != null && next.getOpcode() == INVOKESTATIC && next instanceof MethodInsnNode) {
                    instructions.insertBefore(next, new MethodInsnNode(INVOKESPECIAL, FASTUTIL_INT_2_OBJECT_HASH_MAP, "<init>", "(I)V", false));
                    instructions.remove(next);
                } else {
                    throw new RuntimeException("Unexpected instruction found in OreDictionary.<clinit>");
                }
    
            } else if (node.getOpcode() == INVOKESTATIC && node instanceof MethodInsnNode mNode && mNode.name.equals("initVanillaEntries")) {
                // Call setDefaultReturnValue(-1) - needs to go before the call to initVanillaEntries()
                modified = true;

                // Add EMPTY_INT_ARRAY field initialization
                InsnList newInstrList = new InsnList();
                newInstrList.add(new InsnNode(ICONST_0));
                newInstrList.add(new IntInsnNode(NEWARRAY, T_INT));
                newInstrList.add(new FieldInsnNode(PUTSTATIC, ORE_DICTIONARY, "EMPTY_INT_ARRAY", "[I"));

                newInstrList.add(new FieldInsnNode(GETSTATIC, ORE_DICTIONARY, "nameToId", "Ljava/util/Map;"));
                newInstrList.add(new TypeInsnNode(CHECKCAST, FASTUTIL_OBJECT_2_INT_HASH_MAP));
                newInstrList.add(new InsnNode(ICONST_M1));
                newInstrList.add(new MethodInsnNode(INVOKEVIRTUAL, FASTUTIL_OBJECT_2_INT_HASH_MAP, "defaultReturnValue", "(I)V", false));

                instructions.insertBefore(node, newInstrList);
            }
        }

        return modified;
    }

    private boolean transformGetOreIDStringMethod(MethodNode method) {
        // NOTE: NotEnoughItems also ASMs this method to add synchronization - this should work with and without that
        InsnList instructions = method.instructions;
        boolean modified = false;

        // Get an iterator to traverse through the instructions
        ListIterator<AbstractInsnNode> iterator = instructions.iterator();

        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == GETSTATIC && node instanceof FieldInsnNode fNode && fNode.owner.equals(ORE_DICTIONARY) && fNode.name.equals("nameToId")) {
                modified = true;
                instructions.insert(node, new TypeInsnNode(CHECKCAST, FASTUTIL_OBJECT_2_INT_HASH_MAP));
                AbstractInsnNode next = node.getNext();
                while (next != null && next.getOpcode() != INVOKEINTERFACE) {
                    next = next.getNext();
                }
                if (!(next instanceof MethodInsnNode mNext) || !mNext.owner.equals(JAVA_MAP)) {
                    throw new RuntimeException("Unexpected instruction found in OreDictionary.getOreID(String) method");
                }
                mNext.setOpcode(INVOKEVIRTUAL);
                mNext.owner = FASTUTIL_OBJECT_2_INT_HASH_MAP;
                mNext.itf = false;
                ArrayList<AbstractInsnNode> toRemove = new java.util.ArrayList<>();
                if (mNext.name.equals("put")) {
                    mNext.desc = "(Ljava/lang/Object;I)I";
                } else if (mNext.name.equals("get")) {
                    mNext.name = "getInt";
                    mNext.desc = "(Ljava/lang/Object;)I";
                    while(next != null && next.getOpcode() != CHECKCAST) {
                        next = next.getNext();
                    }
                    if (next != null && next.getOpcode() == CHECKCAST) {
                        toRemove.add(next);
                    }
                    while(next != null && next.getOpcode() != ASTORE) {
                        next = next.getNext();
                    }
                    if (next.getOpcode() == ASTORE && next instanceof VarInsnNode vNode && vNode.var == 1) {
                        vNode.setOpcode(ISTORE);
                    }
                    while(next != null && next.getOpcode() != ALOAD) {
                        next = next.getNext();
                    }

                    if (next != null && next.getOpcode() == ALOAD && next instanceof VarInsnNode vNode && vNode.var == 1) {
                        vNode.setOpcode(ILOAD);
                    }
                    while(next != null && next.getOpcode() != IFNONNULL) {
                        next = next.getNext();
                    }
                    if (next instanceof JumpInsnNode jNode) {
                        jNode.setOpcode(IF_ICMPNE);
                        instructions.insertBefore(next, new InsnNode(ICONST_M1));
                    }
                } else {
                    throw new RuntimeException("Unexpected instruction found in OreDictionary.getOreID(String) method");
                }
                for(AbstractInsnNode toRemoveNode : toRemove) {
                    instructions.remove(toRemoveNode);
                }
    
            } else if ((node.getOpcode() == INVOKESTATIC || node.getOpcode() == INVOKEVIRTUAL) && node instanceof MethodInsnNode mNode && mNode.owner.equals(
                    INTEGER) && (mNode.name.equals("valueOf") || mNode.name.equals("intValue"))) {
                // Remove any calls to INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
                // as well as any INVOKEVIRTUAL java/lang/Integer.intValue ()I;
                modified = true;
                instructions.remove(node);
            } else if (node.getOpcode() == ASTORE && node instanceof VarInsnNode vNode && vNode.var > 0) {
                // change any ASTORE to ISTORE, and any ALOAD to ILOAD
                modified = true;
                vNode.setOpcode(ISTORE);
            } else if (node.getOpcode() == ALOAD && node instanceof VarInsnNode vNode && vNode.var > 0) {
                modified = true;
                vNode.setOpcode(ILOAD);
            }

        }

        return modified;
    }

    private boolean transformGetOreIDItemStackMethod(MethodNode method) {
        InsnList instructions = method.instructions;
        boolean modified = false;

        ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == CHECKCAST && node instanceof TypeInsnNode checkCastNode && checkCastNode.desc.equals("java/util/List")) {
                modified = true;
                checkCastNode.desc = "it/unimi/dsi/fastutil/ints/IntList";
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals("java/util/List")) {
                if (methodNode.name.equals("size")) {
                    modified = true;
                    methodNode.owner = "it/unimi/dsi/fastutil/ints/IntList";
                } else if (methodNode.name.equals("get")) {
                    modified = true;
                    methodNode.owner = "it/unimi/dsi/fastutil/ints/IntList";
                    methodNode.name = "getInt";
                    methodNode.desc = "(I)I";
                }
            } else if (node.getOpcode() == CHECKCAST && node instanceof TypeInsnNode checkCastNode && checkCastNode.desc.equals("java/lang/Integer")) {
                modified = true;
                instructions.remove(node);
            } else if (node.getOpcode() == INVOKEVIRTUAL && node instanceof MethodInsnNode methodNode
                    && methodNode.owner.equals(INTEGER) && methodNode.name.equals("intValue"))
            {
                modified = true;
                instructions.remove(node);
            } else if (node.getOpcode() == INVOKESTATIC && node instanceof MethodInsnNode methodNode
                    && methodNode.owner.equals(INTEGER) && methodNode.name.equals("valueOf"))
            {
                modified = true;
                instructions.remove(node);
            } else if (node.getOpcode() == GETSTATIC && node instanceof FieldInsnNode fieldNode
                    && fieldNode.owner.equals(ORE_DICTIONARY) && fieldNode.name.equals("stackToId"))
            {
                modified = true;
                instructions.insert(node, new TypeInsnNode(CHECKCAST, FASTUTIL_INT_2_OBJECT_HASH_MAP));
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode
                    && methodNode.owner.equals(JAVA_MAP) && methodNode.name.equals("get"))
            {
                modified = true;
                methodNode.setOpcode(INVOKEVIRTUAL);
                methodNode.owner = FASTUTIL_INT_2_OBJECT_HASH_MAP;
                methodNode.desc = "(I)Ljava/lang/Object;";
                methodNode.itf = false;
            }

        }

        return modified;
    }

    private boolean transformGetOresMethod(MethodNode method) {
        InsnList instructions = method.instructions;
        boolean modified = false;

        ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == GETSTATIC && node instanceof FieldInsnNode fNode && fNode.owner.equals(ORE_DICTIONARY) && fNode.name.equals("nameToId")) {
                modified = true;
                instructions.insert(node, new TypeInsnNode(CHECKCAST, FASTUTIL_OBJECT_2_INT_HASH_MAP));
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(JAVA_MAP)
                    && methodNode.name.equals("get"))
            {
                modified = true;
                instructions.insertBefore(node, new InsnNode(ICONST_M1));
                methodNode.setOpcode(INVOKEVIRTUAL);
                methodNode.owner = FASTUTIL_OBJECT_2_INT_HASH_MAP;
                methodNode.name = "getOrDefault";
                methodNode.desc = "(Ljava/lang/Object;I)I";
                methodNode.itf = false;

                AbstractInsnNode next = node.getNext();
                while (next != null && next.getOpcode() != IFNULL) {
                    next = next.getNext();
                }
                if (next instanceof JumpInsnNode jNode) {
                    jNode.setOpcode(IF_ICMPEQ);
                    instructions.insertBefore(next, new InsnNode(ICONST_M1));
                }
            }
        }

        return modified;
    }

    private boolean transformGetOreIDsMethod(MethodNode method) {
        InsnList instructions = method.instructions;
        boolean modified = false;

        final Label returnLabel = new Label();
        final Label toIntArrayBeforeReturnLabel = new Label();

        final LabelNode returnLabelNode = new LabelNode(returnLabel);
        final LabelNode toIntArrayBeforeReturnLabelNode = new LabelNode(toIntArrayBeforeReturnLabel);

        List<AbstractInsnNode> toRemove = new java.util.ArrayList<>();
        ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        boolean foundFirstReturn = false;
        
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            
            if (!foundFirstReturn && node instanceof MethodInsnNode getItemMethodNode && getItemMethodNode.getOpcode() == INVOKEVIRTUAL
                    && getItemMethodNode.owner.equals(itemStackClass) && getItemMethodNode.name.equals("getItem")
                    && getItemMethodNode.desc.equals("()L" + itemClass + ";")) 
            {
                foundFirstReturn = true;
                modified = true;

                // Find and remove ICONST_0 through ARETURN sequence
                AbstractInsnNode next = node.getNext();
                while (next != null && next.getOpcode() != ICONST_0) {
                    next = next.getNext();
                }
                if (next == null) throw new RuntimeException("Could not find ICONST_0");

                // Remove instructions from ICONST_0 through ARETURN
                AbstractInsnNode current = next;
                while (current != null && current.getOpcode() != ARETURN) {
                    toRemove.add(current);
                    current = current.getNext();
                }
                if (current == null || current.getOpcode() != ARETURN) throw new RuntimeException("Could not find ARETURN");

                // Insert GETSTATIC before ARETURN
                instructions.insertBefore(current, new FieldInsnNode(GETSTATIC, ORE_DICTIONARY, "EMPTY_INT_ARRAY", "[I"));

            } else if (node instanceof MethodInsnNode methodNode && methodNode.getOpcode() == INVOKESTATIC && methodNode.owner.equals(
                    "cpw/mods/fml/common/FMLLog") && methodNode.name.equals("log") && methodNode.desc.equals("(Lorg/apache/logging/log4j/Level;Ljava/lang/String;[Ljava/lang/Object;)V"))
            {
                modified = true;
                AbstractInsnNode next = iterator.next();
                while(next != null && next.getOpcode() != ARETURN) {
                    toRemove.add(next);
                    next = next.getNext();
                }

                instructions.insert(node, new FieldInsnNode(GETSTATIC, ORE_DICTIONARY, "EMPTY_INT_ARRAY", "[I"));
            } else if (node.getOpcode() == NEW && node instanceof TypeInsnNode tNode && tNode.desc.equals(JAVA_HASH_SET)) {
                modified = true;
                tNode.desc = FASTUTIL_INT_OPEN_HASH_SET;
            } else if (node.getOpcode() == INVOKESPECIAL && node instanceof MethodInsnNode mNode && mNode.owner.equals(JAVA_HASH_SET)) {
                modified = true;
                mNode.owner = FASTUTIL_INT_OPEN_HASH_SET;
            } else if (node.getOpcode() == GETSTATIC && node instanceof FieldInsnNode fNode && fNode.owner.equals(ORE_DICTIONARY) && fNode.name.equals("stackToId")) {
                modified = true;
                instructions.insert(node, new TypeInsnNode(CHECKCAST, FASTUTIL_INT_2_OBJECT_HASH_MAP));
            } else if (node.getOpcode() == INVOKESTATIC && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(INTEGER) && methodNode.name.equals("valueOf")) {
                modified = true;
                toRemove.add(node);
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(JAVA_MAP) && methodNode.name.equals("get")) {
                modified = true;
                methodNode.setOpcode(INVOKEVIRTUAL);
                methodNode.owner = FASTUTIL_INT_2_OBJECT_HASH_MAP;
                methodNode.desc = "(I)Ljava/lang/Object;";
                methodNode.itf = false;
            } else if (node.getOpcode() == CHECKCAST && node instanceof TypeInsnNode checkCastNode && checkCastNode.desc.equals("java/util/List")) {
                modified = true;
                checkCastNode.desc = "it/unimi/dsi/fastutil/ints/IntList";
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals("java/util/Set") && methodNode.name.equals("addAll")) {
                modified = true;
                methodNode.setOpcode(INVOKEVIRTUAL);
                methodNode.owner = FASTUTIL_INT_OPEN_HASH_SET;
                methodNode.desc = "(L" + FASTUTIL_INT_COLLECTION + ";)Z";
                methodNode.itf = false;
            }  else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals("java/util/Set") && methodNode.name.equals("size")) {
                modified = true;

                // First - go back to the ALOAD before this instruction and add the block
                AbstractInsnNode loadNode = node.getPrevious();

                InsnList newInstructions = new InsnList();
                newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, FASTUTIL_INT_OPEN_HASH_SET, "isEmpty", "()Z", false));
                newInstructions.add(new JumpInsnNode(IFEQ, toIntArrayBeforeReturnLabelNode));
                newInstructions.add(new FieldInsnNode(GETSTATIC, ORE_DICTIONARY, "EMPTY_INT_ARRAY", "[I"));
                newInstructions.add(new JumpInsnNode(GOTO, returnLabelNode));
                newInstructions.add(toIntArrayBeforeReturnLabelNode); // This goes before the ALOAD and after the GOTO


                instructions.insertBefore(loadNode, newInstructions);

                // Then delete everything after this node until ARETURN
                AbstractInsnNode current = node;
                while (current != null && current.getOpcode() != ARETURN) {
                    AbstractInsnNode next = current.getNext();
                    toRemove.add(current);
                    current = next;
                }
                if(current == null || current.getOpcode() != ARETURN) {
                    throw new RuntimeException("Unexpected opcode found in Set.size()");
                }
                // Current should be the last ARETURN instruction
                InsnList insnList = new InsnList();
                insnList.add(new MethodInsnNode(INVOKEVIRTUAL, FASTUTIL_INT_OPEN_HASH_SET, "toIntArray", "()[I", false));
                insnList.add(returnLabelNode);

                instructions.insertBefore(current, insnList);

            }

        }

        for(AbstractInsnNode toRemoveNode : toRemove) {
            instructions.remove(toRemoveNode);
        }

        return modified;
    }


    /**
     * Replaces the bytecode of a method with a specific implementation for getOreIDs.
     * This function is designed for use when Common.thermosTainted is true.
     *
     * @param method The method node to transform
     * @return true if transformation was successful, false otherwise
     */
    private boolean overwriteGetOreIDsMethod(MethodNode method) {
        if (!method.name.equals("getOreIDs") || !method.desc.equals("(L" + itemStackClass + ";)[I")) {
            return false;
        }

        // Clear the existing instructions and local variables
        method.instructions.clear();
        method.localVariables.clear();
        method.tryCatchBlocks.clear();

        // Create new instruction list
        InsnList insns = new InsnList();

        // Create labels
        LabelNode L0 = new LabelNode(new Label());
        LabelNode L1 = new LabelNode(new Label());
        LabelNode L2 = new LabelNode(new Label());
        LabelNode L3 = new LabelNode(new Label());
        LabelNode L4 = new LabelNode(new Label());
        LabelNode L5 = new LabelNode(new Label());
        LabelNode L6 = new LabelNode(new Label());
        LabelNode L7 = new LabelNode(new Label());
        LabelNode L8 = new LabelNode(new Label());
        LabelNode L9 = new LabelNode(new Label());
        LabelNode L10 = new LabelNode(new Label());
        LabelNode L11 = new LabelNode(new Label());
        LabelNode L12 = new LabelNode(new Label());
        LabelNode L13 = new LabelNode(new Label());
        LabelNode L14 = new LabelNode(new Label());

        // Add instructions exactly as in the transformed bytecode on forge
        insns.add(L0);
        insns.add(new LineNumberNode(336, L0));
        insns.add(new VarInsnNode(ALOAD, 0));
        insns.add(new JumpInsnNode(IFNULL, L1));
        insns.add(new VarInsnNode(ALOAD, 0));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, itemStackClass, "getItem", "()L" + itemClass + ";", false));
        insns.add(new JumpInsnNode(IFNONNULL, L2));

        insns.add(L1);
        insns.add(new FieldInsnNode(GETSTATIC, ORE_DICTIONARY, "EMPTY_INT_ARRAY", "[I"));
        insns.add(new InsnNode(ARETURN));

        insns.add(L2);
        insns.add(new LineNumberNode(338, L2));
        insns.add(new TypeInsnNode(NEW, FASTUTIL_INT_OPEN_HASH_SET));
        insns.add(new InsnNode(DUP));
        insns.add(new MethodInsnNode(INVOKESPECIAL, FASTUTIL_INT_OPEN_HASH_SET, "<init>", "()V", false));
        insns.add(new VarInsnNode(ASTORE, 1));

        insns.add(L3);
        insns.add(new LineNumberNode(343, L3));
        insns.add(new VarInsnNode(ALOAD, 0));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, itemStackClass, "getItem", "()L" + itemClass + ";", false));
        insns.add(new FieldInsnNode(GETFIELD, itemClass, "delegate", "Lcpw/mods/fml/common/registry/RegistryDelegate;"));
        insns.add(new MethodInsnNode(INVOKEINTERFACE, "cpw/mods/fml/common/registry/RegistryDelegate", "name", "()Ljava/lang/String;", true));
        insns.add(new VarInsnNode(ASTORE, 2));

        insns.add(L4);
        insns.add(new LineNumberNode(345, L4));
        insns.add(new VarInsnNode(ALOAD, 2));
        insns.add(new JumpInsnNode(IFNONNULL, L5));

        insns.add(L6);
        insns.add(new LineNumberNode(347, L6));
        insns.add(new FieldInsnNode(GETSTATIC, "org/apache/logging/log4j/Level", "DEBUG", "Lorg/apache/logging/log4j/Level;"));
        insns.add(new LdcInsnNode("Attempted to find the oreIDs for an unregistered object (%s). This won't work very well."));
        insns.add(new InsnNode(ICONST_1));
        insns.add(new TypeInsnNode(ANEWARRAY, "java/lang/Object"));
        insns.add(new InsnNode(DUP));
        insns.add(new InsnNode(ICONST_0));
        insns.add(new VarInsnNode(ALOAD, 0));
        insns.add(new InsnNode(AASTORE));
        insns.add(new MethodInsnNode(INVOKESTATIC, "cpw/mods/fml/common/FMLLog", "log", "(Lorg/apache/logging/log4j/Level;Ljava/lang/String;[Ljava/lang/Object;)V", false));
        insns.add(new FieldInsnNode(GETSTATIC, ORE_DICTIONARY, "EMPTY_INT_ARRAY", "[I"));
        insns.add(new InsnNode(ARETURN));

        insns.add(L5);
        insns.add(new LineNumberNode(352, L5));
        insns.add(new MethodInsnNode(INVOKESTATIC, "cpw/mods/fml/common/registry/GameData", "getItemRegistry", "()Lcpw/mods/fml/common/registry/FMLControlledNamespacedRegistry;", false));
        insns.add(new VarInsnNode(ALOAD, 2));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, "cpw/mods/fml/common/registry/FMLControlledNamespacedRegistry", "getId", "(Ljava/lang/String;)I", false));
        insns.add(new VarInsnNode(ISTORE, 3));

        insns.add(L7);
        insns.add(new LineNumberNode(354, L7));
        insns.add(new FieldInsnNode(GETSTATIC, ORE_DICTIONARY, "stackToId", "Ljava/util/Map;"));
        insns.add(new TypeInsnNode(CHECKCAST, FASTUTIL_INT_2_OBJECT_HASH_MAP));
        insns.add(new VarInsnNode(ILOAD, 3));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, FASTUTIL_INT_2_OBJECT_HASH_MAP, "get", "(I)Ljava/lang/Object;", false));
        insns.add(new TypeInsnNode(CHECKCAST, "it/unimi/dsi/fastutil/ints/IntList"));
        insns.add(new VarInsnNode(ASTORE, 4));

        insns.add(L8);
        insns.add(new LineNumberNode(355, L8));
        insns.add(new VarInsnNode(ALOAD, 4));
        insns.add(new JumpInsnNode(IFNULL, L9));
        insns.add(new VarInsnNode(ALOAD, 1));
        insns.add(new VarInsnNode(ALOAD, 4));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, FASTUTIL_INT_OPEN_HASH_SET, "addAll", "(L" + FASTUTIL_INT_COLLECTION + ";)Z", false));
        insns.add(new InsnNode(POP));

        insns.add(L9);
        insns.add(new LineNumberNode(356, L9));
        insns.add(new FieldInsnNode(GETSTATIC, ORE_DICTIONARY, "stackToId", "Ljava/util/Map;"));
        insns.add(new TypeInsnNode(CHECKCAST, FASTUTIL_INT_2_OBJECT_HASH_MAP));
        insns.add(new VarInsnNode(ILOAD, 3));
        insns.add(new VarInsnNode(ALOAD, 0));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, itemStackClass, "getItemDamage", "()I", false));
        insns.add(new InsnNode(ICONST_1));
        insns.add(new InsnNode(IADD));
        insns.add(new IntInsnNode(BIPUSH, 16));
        insns.add(new InsnNode(ISHL));
        insns.add(new InsnNode(IOR));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, FASTUTIL_INT_2_OBJECT_HASH_MAP, "get", "(I)Ljava/lang/Object;", false));
        insns.add(new TypeInsnNode(CHECKCAST, "it/unimi/dsi/fastutil/ints/IntList"));
        insns.add(new VarInsnNode(ASTORE, 4));

        insns.add(L10);
        insns.add(new LineNumberNode(357, L10));
        insns.add(new VarInsnNode(ALOAD, 4));
        insns.add(new JumpInsnNode(IFNULL, L11));
        insns.add(new VarInsnNode(ALOAD, 1));
        insns.add(new VarInsnNode(ALOAD, 4));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, FASTUTIL_INT_OPEN_HASH_SET, "addAll", "(L" + FASTUTIL_INT_COLLECTION + ";)Z", false));
        insns.add(new InsnNode(POP));

        insns.add(L11);
        insns.add(new LineNumberNode(359, L11));
        insns.add(new VarInsnNode(ALOAD, 1));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, FASTUTIL_INT_OPEN_HASH_SET, "isEmpty", "()Z", false));
        insns.add(new JumpInsnNode(IFEQ, L12));
        insns.add(new FieldInsnNode(GETSTATIC, ORE_DICTIONARY, "EMPTY_INT_ARRAY", "[I"));
        insns.add(new JumpInsnNode(GOTO, L13));

        insns.add(L12);
        insns.add(new VarInsnNode(ALOAD, 1));
        insns.add(new MethodInsnNode(INVOKEVIRTUAL, FASTUTIL_INT_OPEN_HASH_SET, "toIntArray", "()[I", false));

        insns.add(L13);
        insns.add(new InsnNode(ARETURN));

        insns.add(L14);

        // Set up local variables
        method.localVariables.add(new LocalVariableNode("x", "I", null, L0, L0, 7));
        method.localVariables.add(new LocalVariableNode("stack", "L" + itemStackClass + ";", null, L0, L14, 0));
        method.localVariables.add(new LocalVariableNode("set", "Ljava/util/Set;", "Ljava/util/Set<Ljava/lang/Integer;>;", L3, L14, 1));
        method.localVariables.add(new LocalVariableNode("registryName", "Ljava/lang/String;", null, L4, L14, 2));
        method.localVariables.add(new LocalVariableNode("id", "I", null, L7, L14, 3));
        method.localVariables.add(new LocalVariableNode("ids", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/Integer;>;", L8, L14, 4));
        method.localVariables.add(new LocalVariableNode("tmp", "[Ljava/lang/Integer;", null, L0, L14, 5));
        method.localVariables.add(new LocalVariableNode("ret", "[I", null, L0, L14, 6));

        // Set the max stack and locals
        method.maxStack = 6;
        method.maxLocals = 8;

        // Add the instructions to the method
        method.instructions = insns;

        return true;
    }


    private boolean transformGetOresIntMethod (MethodNode method){
        boolean modified = false;
        
        InsnList instructions = method.instructions;
        ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == GETSTATIC && node instanceof FieldInsnNode fNode && fNode.owner.equals(ORE_DICTIONARY) && fNode.name.equals("nameToId")) {
                modified = true;
                instructions.insert(node, new TypeInsnNode(CHECKCAST, FASTUTIL_OBJECT_2_INT_HASH_MAP));
            } else if (node.getOpcode() == INVOKESTATIC && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(INTEGER)
                    && methodNode.name.equals("valueOf")) {
                modified = true;
                instructions.remove(node);
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(JAVA_MAP) && methodNode.name.equals("put")) {
                modified = true;
                methodNode.setOpcode(INVOKEVIRTUAL);
                methodNode.owner = FASTUTIL_OBJECT_2_INT_HASH_MAP;
                methodNode.name = "put";
                methodNode.desc = "(Ljava/lang/Object;I)I";
                methodNode.itf = false;
            }

        }
        return modified;
    }

    private boolean transformRegisterOreImplMethod(MethodNode method) {
        InsnList instructions = method.instructions;
        boolean modified = false;

        ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            if (node.getOpcode() == GETSTATIC && node instanceof FieldInsnNode fNode && fNode.owner.equals(ORE_DICTIONARY) && fNode.name.equals("stackToId")) {
                modified = true;
                instructions.insert(node, new TypeInsnNode(CHECKCAST, FASTUTIL_INT_2_OBJECT_HASH_MAP));
            } else if (node.getOpcode() == INVOKESTATIC && node instanceof MethodInsnNode methodNode
                    && methodNode.owner.equals(INTEGER) && methodNode.name.equals("valueOf")) 
            {
                modified = true;
                instructions.remove(node);
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(JAVA_MAP) && (methodNode.name.equals("get"))) {
                modified = true;
                methodNode.setOpcode(INVOKEVIRTUAL);
                methodNode.owner = FASTUTIL_INT_2_OBJECT_HASH_MAP;
                methodNode.desc = "(I)Ljava/lang/Object;";
                methodNode.itf = false;

                AbstractInsnNode next = node.getNext();
                while (next != null && next.getOpcode() != CHECKCAST) {
                    next = next.getNext();
                }
                if ( next != null && next.getOpcode() == CHECKCAST && next instanceof TypeInsnNode checkCastNode && checkCastNode.desc.equals("java/util/List")) {
                    checkCastNode.desc = "it/unimi/dsi/fastutil/ints/IntList";
                }
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals("java/util/List")
                    && methodNode.name.equals("contains")) {
                modified = true;
                methodNode.owner = "it/unimi/dsi/fastutil/ints/IntList";
                methodNode.desc = "(I)Z";
            } else if (node.getOpcode() == INVOKESTATIC && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(
                    "com/google/common/collect/Lists") && methodNode.name.equals("newArrayList") && methodNode.desc.equals("()Ljava/util/ArrayList;")) {
                modified = true;
                instructions.insertBefore(node, new TypeInsnNode(NEW, "it/unimi/dsi/fastutil/ints/IntArrayList"));
                instructions.insertBefore(node, new InsnNode(DUP));
                instructions.insertBefore(node, new MethodInsnNode(INVOKESPECIAL, "it/unimi/dsi/fastutil/ints/IntArrayList", "<init>", "()V", false));
                instructions.remove(node);
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode
                    && methodNode.owner.equals(JAVA_MAP) && methodNode.name.equals("put")) {
                modified = true;
                methodNode.setOpcode(INVOKEVIRTUAL);
                methodNode.owner = FASTUTIL_INT_2_OBJECT_HASH_MAP;
                methodNode.desc = "(ILjava/lang/Object;)Ljava/lang/Object;";
                methodNode.itf = false;
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode
                    && methodNode.owner.equals("java/util/List") && methodNode.name.equals("add")) {
                modified = true;
                methodNode.owner = "it/unimi/dsi/fastutil/ints/IntList";
                methodNode.desc = "(I)Z";
            }

        }
        return modified;
    }

    private boolean transformRebakeMapMethod(MethodNode method) {
        InsnList instructions = method.instructions;
        boolean modified = false;
        boolean inIdToStackContext = false;

        ListIterator<AbstractInsnNode> iterator = instructions.iterator();
        while (iterator.hasNext()) {
            AbstractInsnNode node = iterator.next();
            // Check if we're working with stackToId or idToStack
            if (node.getOpcode() == GETSTATIC && node instanceof FieldInsnNode fieldNode) {
                if (fieldNode.owner.equals(ORE_DICTIONARY) && fieldNode.name.equals("stackToId")) {
                    inIdToStackContext = false;
                } else if (fieldNode.owner.equals(ORE_DICTIONARY) && fieldNode.name.equals("idToStack")) {
                    inIdToStackContext = true;
                }
            }

            if (node.getOpcode() == INVOKESTATIC && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(
                    "com/google/common/collect/Lists") && methodNode.name.equals("newArrayList") && methodNode.desc.equals("()Ljava/util/ArrayList;")) {
                // Only transform ArrayList creation if we're in stackToId context or not in idToStack context
                if (!inIdToStackContext) {
                    modified = true;
                    instructions.insertBefore(node, new TypeInsnNode(NEW, "it/unimi/dsi/fastutil/ints/IntArrayList"));
                    instructions.insertBefore(node, new InsnNode(DUP));
                    instructions.insertBefore(node, new MethodInsnNode(INVOKESPECIAL, "it/unimi/dsi/fastutil/ints/IntArrayList", "<init>", "()V", false));
                    instructions.remove(node);
                }
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals("java/util/List") && methodNode.name.equals("add") && !inIdToStackContext) {
                modified = true;
                methodNode.owner = "it/unimi/dsi/fastutil/ints/IntList";
                methodNode.desc = "(I)Z";
            } else if (node.getOpcode() == CHECKCAST && node instanceof TypeInsnNode checkCastNode && checkCastNode.desc.equals("java/util/List") && !inIdToStackContext) {
                modified = true;
                checkCastNode.desc = "it/unimi/dsi/fastutil/ints/IntList";
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals("java/util/List") && methodNode.name.equals("contains")) {
                modified = true;
                methodNode.owner = "it/unimi/dsi/fastutil/ints/IntList";
                methodNode.desc = "(I)Z";
            } else if (node.getOpcode() == INVOKESTATIC && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(INTEGER) && methodNode.name.equals("valueOf")) {
                modified = true;
                instructions.remove(node);
            } else if (node.getOpcode() == INVOKEVIRTUAL && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(INTEGER) && methodNode.name.equals("intValue")) {
                modified = true;
                instructions.remove(node);
            } else if (node.getOpcode() == CHECKCAST && node instanceof TypeInsnNode checkCastNode && checkCastNode.desc.equals(INTEGER)) {
                modified = true;
                instructions.remove(node);
            } else if (node.getOpcode() == GETSTATIC && node instanceof FieldInsnNode fNode && fNode.owner.equals(ORE_DICTIONARY) && fNode.name.equals("stackToId")) {
                modified = true;
                instructions.insert(node, new TypeInsnNode(CHECKCAST, FASTUTIL_INT_2_OBJECT_HASH_MAP));
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(JAVA_MAP) && methodNode.name.equals("put")) {
                modified = true;
                methodNode.setOpcode(INVOKEVIRTUAL);
                methodNode.owner = FASTUTIL_INT_2_OBJECT_HASH_MAP;
                methodNode.desc = "(ILjava/lang/Object;)Ljava/lang/Object;";
                methodNode.itf = false;
            } else if (node.getOpcode() == INVOKEINTERFACE && node instanceof MethodInsnNode methodNode && methodNode.owner.equals(JAVA_MAP) && methodNode.name.equals("get")) {
                modified = true;
                methodNode.setOpcode(INVOKEVIRTUAL);
                methodNode.owner = FASTUTIL_INT_2_OBJECT_HASH_MAP;
                methodNode.desc = "(I)Ljava/lang/Object;";
                methodNode.itf = false;
            }
        }

        return modified;
    }

}
// spotless:on
