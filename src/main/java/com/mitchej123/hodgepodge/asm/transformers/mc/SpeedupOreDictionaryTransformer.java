package com.mitchej123.hodgepodge.asm.transformers.mc;

import static org.objectweb.asm.Opcodes.ACC_FINAL;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_STATIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ARETURN;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.GOTO;
import static org.objectweb.asm.Opcodes.ICONST_0;
import static org.objectweb.asm.Opcodes.ICONST_M1;
import static org.objectweb.asm.Opcodes.IFEQ;
import static org.objectweb.asm.Opcodes.IFNULL;
import static org.objectweb.asm.Opcodes.IF_ICMPEQ;
import static org.objectweb.asm.Opcodes.IF_ICMPNE;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.NEWARRAY;
import static org.objectweb.asm.Opcodes.PUTSTATIC;
import static org.objectweb.asm.Opcodes.T_INT;

import java.util.List;
import java.util.ListIterator;

import com.mitchej123.hodgepodge.Common;
import net.minecraft.launchwrapper.IClassTransformer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

/**
 * Transformer that optimizes OreDictionary by replacing boxed Integer types with primitives and swapping standard Java
 * collections with fastutil equivalents that are optimized for primitive types.
 */
// Spotless keeps indenting each else-if to the right every time
// spotless:off
public class SpeedupOreDictionaryTransformer implements IClassTransformer {

    public static final String JAVA_HASH_SET = "java/util/HashSet";

    private static final Logger LOGGER = LogManager.getLogger("SpeedupOreDictionaryTransformer");
    private static final Printer printer = new Textifier();
    private static final TraceMethodVisitor mp = new TraceMethodVisitor(printer);

    private static final String ORE_DICTIONARY = "net/minecraftforge/oredict/OreDictionary";

    private static final String JAVA_HASH_MAP = "java/util/HashMap";
    private static final String JAVA_MAP = "java/util/Map";
    private static final String FASTUTIL_INT_OPEN_HASH_SET = "it/unimi/dsi/fastutil/ints/IntOpenHashSet";
    private static final String FASTUTIL_INT_COLLECTION = "it/unimi/dsi/fastutil/ints/IntCollection";
    private static final String FASTUTIL_OBJECT_2_INT_HASH_MAP = "it/unimi/dsi/fastutil/objects/Object2IntOpenHashMap";
    private static final String FASTUTIL_INT_2_OBJECT_HASH_MAP = "it/unimi/dsi/fastutil/ints/Int2ObjectOpenHashMap";
    private static final String INTEGER = "java/lang/Integer";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) return null;

        if (transformedName.equals("net.minecraftforge.oredict.OreDictionary")) {
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
        for (MethodNode method : classNode.methods) {
            if ("<clinit>".equals(method.name)) {
                modified |= transformClinitMethod(method);
            } else if ("getOreID".equals(method.name) && "(Ljava/lang/String;)I".equals(method.desc)) {
                modified |= transformGetOreIDStringMethod(method);
            } else if ("getOreID".equals(method.name) && "(Lnet/minecraft/item/ItemStack;)I".equals(method.desc)) {
                modified |= transformGetOreIDItemStackMethod(method);
            } else if ("getOreIDs".equals(method.name) && "(Lnet/minecraft/item/ItemStack;)[I".equals(method.desc) && !Common.thermosTainted) {
                modified |= transformGetOreIDsMethod(method);
            } else if ("getOres".equals(method.name) && "(Ljava/lang/String;Z)Ljava/util/List;".equals(method.desc)) {
                modified |= transformGetOresMethod(method);
            } else if ("getOres".equals(method.name) && "(I)Ljava/util/ArrayList;".equals(method.desc)) {
                modified |= transformGetOresIntMethod(method);
            } else if ("registerOreImpl".equals(method.name) && "(Ljava/lang/String;Lnet/minecraft/item/ItemStack;)V".equals(method.desc)) {
                modified |= transformRegisterOreImplMethod(method);
            }

        }

        if (modified) {
            try {
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
                if (mNext.name.equals("put")) {
                    mNext.desc = "(Ljava/lang/Object;I)I";
                } else if (mNext.name.equals("get")) {
                    mNext.name = "getInt";
                    mNext.desc = "(Ljava/lang/Object;)I";
    
                    AbstractInsnNode checkCastNode = next.getNext();
                    next = checkCastNode.getNext();
                    instructions.remove(checkCastNode);
                    if (next.getOpcode() == ASTORE && next instanceof VarInsnNode vNode && vNode.var == 1) {
                        vNode.setOpcode(ISTORE);
                    }
                    next = next.getNext();
                    if (next.getOpcode() == ALOAD && next instanceof VarInsnNode vNode && vNode.var == 1) {
                        vNode.setOpcode(ILOAD);
                    }
                    next = next.getNext(); // IFNONNULL --> IF_ICMPNE
                    if (next instanceof JumpInsnNode jNode) {
                        jNode.setOpcode(IF_ICMPNE);
                    }
                    instructions.insertBefore(next, new InsnNode(ICONST_M1));
                } else {
                    throw new RuntimeException("Unexpected instruction found in OreDictionary.getOreID(String) method");
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
                    && getItemMethodNode.owner.equals("net/minecraft/item/ItemStack") && getItemMethodNode.name.equals("getItem")
                    && getItemMethodNode.desc.equals("()Lnet/minecraft/item/Item;")) 
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
                int count = 0;
                while(next != null && next.getOpcode() != ARETURN) {
                    toRemove.add(next);
                    next = next.getNext();
                    count++;
                }
                if (count != 2) throw new RuntimeException("Unexpected number of instructions found in FMLLog.log");

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
                int count = 0;
                while (current != null && current.getOpcode() != ARETURN) {
                    
                    AbstractInsnNode next = current.getNext();
                    toRemove.add(current);
                    current = next;
                    count++;
                }
                if(count != 29 /* 27 instructions, 2 frames */) {
                    throw new RuntimeException("Unexpected number of instructions found in Set.size()");
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
                if (next.getOpcode() == CHECKCAST && next instanceof TypeInsnNode checkCastNode && checkCastNode.desc.equals("java/util/List")) {
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

}
// spotless:on
