package com.mitchej123.hodgepodge.core.fml.hooks.fml;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;
import org.objectweb.asm.signature.SignatureWriter;
import org.objectweb.asm.tree.ClassNode;

import cpw.mods.fml.relauncher.FMLRelaunchLog;

@SuppressWarnings("unused")
public class ModAPITransformerHook {

    public static void stripInterfaceFromSignature(ClassNode classNode, String ifaceInternalName) {
        if (classNode == null || classNode.signature == null) return;
        final String original = classNode.signature;
        if (original.indexOf(ifaceInternalName) < 0) return;

        final SignatureWriter writer = new SignatureWriter();
        final Rewriter rewriter = new Rewriter(ifaceInternalName, writer);

        try {
            new SignatureReader(original).accept(rewriter);
        } catch (Exception e) {
            FMLRelaunchLog.warning(
                    "Optional removal - failed to scan Signature for %s (interface %s): %s",
                    classNode.name,
                    ifaceInternalName,
                    e);
            return;
        }

        if (!rewriter.removed) return;

        final String rewritten = writer.toString();
        classNode.signature = rewritten.isEmpty() ? null : rewritten;
        FMLRelaunchLog.finer(
                "Optional removal - rewrote Signature for %s to drop interface %s",
                classNode.name,
                ifaceInternalName);
    }

    private static final class Rewriter extends SignatureVisitor {

        final String toRemove;
        final SignatureWriter out;
        final StringBuilder currentInterfaceName = new StringBuilder(64);
        SignatureWriter currentInterface;
        boolean removed;

        Rewriter(String toRemove, SignatureWriter out) {
            super(Opcodes.ASM5);
            this.toRemove = toRemove;
            this.out = out;
        }

        @Override
        public void visitFormalTypeParameter(String n) {
            out.visitFormalTypeParameter(n);
        }

        @Override
        public SignatureVisitor visitClassBound() {
            return out.visitClassBound();
        }

        @Override
        public SignatureVisitor visitInterfaceBound() {
            return out.visitInterfaceBound();
        }

        @Override
        public SignatureVisitor visitSuperclass() {
            return out.visitSuperclass();
        }

        @Override
        public SignatureVisitor visitInterface() {
            currentInterfaceName.setLength(0);
            currentInterface = new SignatureWriter();
            return this;
        }

        @Override
        public void visitClassType(String n) {
            currentInterfaceName.append(n);
            currentInterface.visitClassType(n);
        }

        @Override
        public void visitInnerClassType(String n) {
            currentInterfaceName.append('$').append(n);
            currentInterface.visitInnerClassType(n);
        }

        @Override
        public void visitTypeArgument() {
            currentInterface.visitTypeArgument();
        }

        @Override
        public SignatureVisitor visitTypeArgument(char wildcard) {
            return currentInterface.visitTypeArgument(wildcard);
        }

        @Override
        public void visitEnd() {
            currentInterface.visitEnd();
            if (toRemove.contentEquals(currentInterfaceName)) {
                // We're removing this, drop the copy
                currentInterface = null;
                removed = true;
                return;
            }
            // We're not removing it - replay currentInterface as events into out.
            new SignatureReader(currentInterface.toString()).acceptType(out.visitInterface());
        }
    }
}
