package com.mitchej123.hodgepodge.core.shared.transformers;

import com.gtnewhorizon.gtnhlib.asm.ClassConstantPoolParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public final class PrimitiveCallBacksTransformer implements Opcodes {
    // TODO could this be a post transform in a mixin plugin?
    private static final String CIR_NAME = "org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable";
    private static final String CIR_DESC = "L" + CIR_NAME + ";";
    private final ClassConstantPoolParser cstPoolParser = new ClassConstantPoolParser(CIR_NAME);

    public @NotNull String @Nullable [] getTransformerExclusions() {
        return new String[]{"org.spongepowered"};
    }

    public boolean shouldTransform(byte[] originalBytes) {
        return cstPoolParser.find(originalBytes);
    }

    public boolean transformClassNode(@NotNull String className, ClassNode cn) {
        final Map<String, Handler> candidateHandlers = findCandidateHandlers(cn);
        if (candidateHandlers == null) {
            return false;
        }
        boolean found = findHandlerCallSites(cn, candidateHandlers);
        if (!found) {
            return false;
        }
        boolean changed = false;
        for (Handler handler : candidateHandlers.values()) {
            if (handler.callSites != null) {
                handler.transform();
                for (HandlerCallSite callSite : handler.callSites) {
                    callSite.transform(handler.type);
                }
                changed = true;
            }
        }
        return changed;
    }

    private static class Handler {

        /**
         * The method node that represents this handler
         */
        private final MethodNode methodNode;

        /**
         * The generic type of the CallbackInfoReturnable
         */
        Type type;

        /**
         * List of calls to this handler
         */
        List<HandlerCallSite> callSites;

        public Handler(MethodNode methodNode) {
            this.methodNode = methodNode;
        }

        public void addCallSite(HandlerCallSite callSite) {
            if (callSites == null) callSites = new ArrayList<>();
            callSites.add(callSite);
        }

        public void transform() {
            // TODO transform the handler method
            // change the descriptor
            // if signature not null change the signature
            // if localvariabletable exists change the type of the
            // changer toutes les method nodze qui ont pour owner CIR_NAME

            // if we have
            // CallbackInfoReturnable.getReturnValue ()Ljava/lang/Object; // faut changer owner + descriptor + method name
            // ensuite ya CHECKCAST java/lang/Integer // il faut le remove
            // si ensuite ya il y a INVOKEVIRTUAL java/lang/Integer.intValue ()I, on le remove
            //      si ya pas il faut rajouer a la main : INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
            //      ajouter log output si on rajoute du boxing

            // if we have
            // CallbackInfoReturnable.setReturnValue (Ljava/lang/Object;)V // faut changer owner + descriptor + method name
            // si la node précédente est INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
            //      si ya pas faut rajouter a la main INVOKEVIRTUAL java/lang/Integer.intValue ()I
            //      ajouter log output si on rajoute du unboxing
        }
    }

    private static class HandlerCallSite {

        /**
         * The method node that contains this handler call
         */
        private final MethodNode methodNode;
        /**
         * The instruction that invokes the mixin handler
         */
        private final MethodInsnNode handlerCall;

        private HandlerCallSite(MethodNode methodNode, MethodInsnNode handlerCall) {
            this.methodNode = methodNode;
            this.handlerCall = handlerCall;
        }

        public void transform(Type type) {
            // TODO
            // for loop vers le haut
            // transformer le type NEW opcode
            // transformer le <init> methonode
            // si il est directement suivi d'un ASTORE {
            //      changer le type dans la local variable table
            // }
            // changer le desc de this.handlerCall
            // for loop dans les instructions ensuite
            // changer tous les method call qui ont pour owner CIR_NAME
            // stop quand on trouver un nouvelle type node NEW CIR_NAME

        }
    }

    private static @Nullable Map<String, Handler> findCandidateHandlers(ClassNode cn) {
        Map<String, Handler> map = null;
        for (MethodNode mn : cn.methods) {
            if (isPrivate(mn) && mn.desc.contains(CIR_DESC) && isUsingCIR(mn) && !isCIREscapingMethod(mn)) {
                if (map == null) {
                    map = new HashMap<>();
                }
                map.put(mn.name + mn.desc, new Handler(mn));
            }
        }
        return map;
    }

    private static boolean findHandlerCallSites(ClassNode cn, Map<String, Handler> candidateHandlers) {
        boolean found = false;
        for (MethodNode mn : cn.methods) {
            final Type returnType = Type.getReturnType(mn.desc);
            if (!isPrimitiveType(returnType)) continue;
            final ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
            while (it.hasNext()) {
                final AbstractInsnNode insn = it.next();
                if (insn instanceof MethodInsnNode mNode && mNode.desc.contains(CIR_DESC)) {
                    final Handler handler = candidateHandlers.get(mNode.name + mNode.desc);
                    if (handler != null) {
                        handler.type = returnType;
                        handler.addCallSite(new HandlerCallSite(mn, mNode));
                        found = true;
                    }
                }
            }
        }
        return found;
    }

    private static int getCIRIndex(MethodNode mn) {
        final Type[] handlerArgs = Type.getArgumentTypes(mn.desc);
        for (int i = 0; i < handlerArgs.length; i++) {
            final Type arg = handlerArgs[i];
            if (arg.getSort() == Type.OBJECT && arg.getDescriptor().equals(CIR_DESC)) {
                return isStatic(mn) ? i : i + 1;
            }
        }
        throw new IllegalArgumentException();
    }

    private static boolean isUsingCIR(MethodNode mn) {
        final int index = getCIRIndex(mn);
        final ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
        while (it.hasNext()) {
            final AbstractInsnNode insn = it.next();
            if (insn.getType() == AbstractInsnNode.VAR_INSN && insn.getOpcode() == ALOAD && ((VarInsnNode) insn).var == index) {
                return true;
            }
        }
        return false;
    }

    private static boolean isCIREscapingMethod(MethodNode mn) {
        final ListIterator<AbstractInsnNode> it = mn.instructions.iterator();
        while (it.hasNext()) {
            final AbstractInsnNode insn = it.next();
            if (insn.getType() == AbstractInsnNode.FIELD_INSN && insn.getOpcode() == PUTFIELD && ((FieldInsnNode) insn).desc.contains(CIR_NAME)) {
                return true;
            } else if (insn.getType() == AbstractInsnNode.METHOD_INSN && ((MethodInsnNode) insn).desc.contains(CIR_DESC)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isPrivate(MethodNode mn) {
        return (mn.access & ACC_PRIVATE) == ACC_PRIVATE;
    }

    private static boolean isStatic(MethodNode mn) {
        return (mn.access & ACC_STATIC) == ACC_STATIC;
    }

    //public boolean transformClassNode(@NotNull String className, ClassNode classNode) {
    //    boolean changed = false;
    //    for (MethodNode mn : classNode.methods) {
    //        final Type returnType = Type.getReturnType(mn.desc);
    //        if (isPrimitiveType(returnType)) {
    //            boolean mnChanged = false;
    //            for (AbstractInsnNode insnNode : mn.instructions.toArray()) {
    //                if (insnNode.getOpcode() == NEW && insnNode instanceof TypeInsnNode typeNode && typeNode.desc.equals(CIR_NAME)) {
    //                    typeNode.desc = getCIRNameForType(returnType);
    //                    mnChanged = true;
    //                } else if (insnNode instanceof MethodInsnNode mNode) {
    //                    if (mNode.owner.equals(CIR_NAME)) {
    //                        mNode.owner = getCIRNameForType(returnType);
    //                        mnChanged = true;
    //                    } else if (mNode.desc.contains(CIR_DESC)) {
    //                        methodCIRTypes.put(mNode.name + "#" + mNode.desc, returnType);
    //                        mNode.desc = mNode.desc.replace(CIR_DESC, getCIRDescForType(returnType));
    //                        mnChanged = true;
    //                    }
    //                }
    //            }
    //            if (mnChanged) {
    //                changed = true;
    //                if (mn.localVariables != null) {
    //                    for (LocalVariableNode local : mn.localVariables) {
    //                        if (local.desc.equals(CIR_DESC)) {
    //                            local.desc = getCIRDescForType(returnType);
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //
    //        final Type type = methodCIRTypes.get(mn.name + "#" + mn.desc);
    //        if (type != null) {
    //            mn.desc =
    //        }
    //
    //    }
    //    return changed;
    //}

    private static boolean isPrimitiveType(Type type) {
        // todo opti don't create return type object,
        //  directly look at the string last character
        final int sort = type.getSort();
        return Type.BOOLEAN <= sort && sort <= Type.DOUBLE;
    }

    private static String getCIRNameForType(Type type) {
        return switch (type.getSort()) {
            case Type.BOOLEAN -> "com/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableZ";
            case Type.CHAR -> "com/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableC";
            case Type.BYTE -> "com/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableB";
            case Type.SHORT -> "com/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableS";
            case Type.INT -> "com/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableI";
            case Type.FLOAT -> "com/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableF";
            case Type.LONG -> "com/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableJ";
            case Type.DOUBLE -> "com/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableD";
            default -> throw new IllegalArgumentException();
        };
    }

    private static String getCIRDescForType(Type type) {
        return switch (type.getSort()) {
            case Type.BOOLEAN -> "Lcom/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableZ;";
            case Type.CHAR -> "Lcom/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableC;";
            case Type.BYTE -> "Lcom/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableB;";
            case Type.SHORT -> "Lcom/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableS;";
            case Type.INT -> "Lcom/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableI;";
            case Type.FLOAT -> "Lcom/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableF;";
            case Type.LONG -> "Lcom/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableJ;";
            case Type.DOUBLE -> "Lcom/mitchej123/hodgepodge/mixins/callback/CallbackInfoReturnableD;";
            default -> throw new IllegalArgumentException();
        };
    }
}
