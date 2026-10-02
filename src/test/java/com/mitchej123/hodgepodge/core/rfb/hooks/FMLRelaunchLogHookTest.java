package com.mitchej123.hodgepodge.core.rfb.hooks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.CheckClassAdapter;

import com.mitchej123.hodgepodge.core.rfb.transformers.FMLRelaunchLogASMHelper;

@Timeout(60)
class FMLRelaunchLogHookTest {

    private static final String FQCN = "cpw.mods.fml.relauncher.FMLRelaunchLog";

    public static final class Probe {
    }

    @BeforeEach
    void clearCache() {
        FMLRelaunchLogHook.clear();
    }

    @Test
    void returnsWhatLog4jWould() {
        Logger stock = LogManager.getLogger("hook.same");
        Logger hooked = FMLRelaunchLogHook.getLogger("hook.same", FMLRelaunchLogHookTest.class);
        assertSame(stock, hooked);
        assertSame(hooked, FMLRelaunchLogHook.getLogger("hook.same", FMLRelaunchLogHookTest.class));
    }

    @Test
    void nullNameUsesFmlRelaunchLog() {
        Logger hooked = FMLRelaunchLogHook.getLogger(null, FMLRelaunchLogHookTest.class);
        assertEquals(FQCN, hooked.getName());
        assertSame(hooked, FMLRelaunchLogHook.getLogger(FQCN, FMLRelaunchLogHookTest.class));
    }

    @Test
    void stoppedContextReResolves() throws Exception {
        try (URLClassLoader iso = isolated()) {
            Class<?> probe = Class.forName(Probe.class.getName(), false, iso);
            Logger before = FMLRelaunchLogHook.getLogger("hook.stop", probe);
            LoggerContext old = (LoggerContext) LogManager.getContext(iso, false);
            old.stop();
            Logger after = FMLRelaunchLogHook.getLogger("hook.stop", probe);
            LoggerContext fresh = (LoggerContext) LogManager.getContext(iso, false);
            assertNotSame(old, fresh);
            assertNotSame(before, after);
            assertSame(fresh.getLogger("hook.stop"), after);
            assertSame(after, FMLRelaunchLogHook.getLogger("hook.stop", probe));
            fresh.stop();
        }
    }

    @Test
    void pastTheCapLookupsStayCorrect() {
        for (int i = 0; i < FMLRelaunchLogHook.CAP; i++) {
            FMLRelaunchLogHook.getLogger("hook.fill." + i, FMLRelaunchLogHookTest.class);
        }
        assertEquals(FMLRelaunchLogHook.CAP, FMLRelaunchLogHook.cacheSize());
        Logger over = FMLRelaunchLogHook.getLogger("hook.over", FMLRelaunchLogHookTest.class);
        assertSame(LogManager.getLogger("hook.over"), over);
        assertSame(over, FMLRelaunchLogHook.getLogger("hook.over", FMLRelaunchLogHookTest.class));
        assertEquals(FMLRelaunchLogHook.CAP, FMLRelaunchLogHook.cacheSize());
    }

    @Test
    void transformerRedirectsExactlyTheTwoTargetLogCalls() throws IOException {
        ClassNode node = readFmlRelaunchLog();
        List<String> untouchedBefore = logManagerCalls(node);
        assertTrue(FMLRelaunchLogASMHelper.transform(node));

        int redirects = 0;
        for (MethodNode m : node.methods) {
            for (AbstractInsnNode insn = m.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (insn instanceof MethodInsnNode call && call.owner.equals(FMLRelaunchLogASMHelper.HOOK)) {
                    redirects++;
                    assertEquals(Opcodes.INVOKESTATIC, call.getOpcode());
                    assertEquals(FMLRelaunchLogASMHelper.HOOK_DESC, call.desc);
                    assertTrue(
                            m.desc.equals(FMLRelaunchLogASMHelper.LOG_DESC)
                                    || m.desc.equals(FMLRelaunchLogASMHelper.LOG_THROWABLE_DESC),
                            m.name + m.desc);
                    LdcInsnNode ldc = assertInstanceOf(LdcInsnNode.class, call.getPrevious());
                    assertEquals(Type.getObjectType(node.name), ldc.cst);
                }
            }
        }
        assertEquals(2, redirects);
        List<String> untouchedAfter = logManagerCalls(node);
        assertEquals(untouchedBefore.size() - 2, untouchedAfter.size());
        for (String site : untouchedAfter) {
            assertFalse(site.startsWith("log("), site);
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(cw);
        StringWriter out = new StringWriter();
        CheckClassAdapter
                .verify(new ClassReader(cw.toByteArray()), getClass().getClassLoader(), false, new PrintWriter(out));
        assertEquals("", out.toString());

        assertFalse(FMLRelaunchLogASMHelper.transform(node));
    }

    private static List<String> logManagerCalls(ClassNode node) {
        List<String> sites = new ArrayList<>();
        for (MethodNode m : node.methods) {
            for (AbstractInsnNode insn = m.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                if (insn instanceof MethodInsnNode call && call.owner.equals("org/apache/logging/log4j/LogManager")
                        && call.name.equals("getLogger")) {
                    sites.add(m.name + m.desc);
                }
            }
        }
        return sites;
    }

    private static ClassNode readFmlRelaunchLog() throws IOException {
        String path = FQCN.replace('.', '/') + ".class";
        try (InputStream in = FMLRelaunchLogHookTest.class.getClassLoader().getResourceAsStream(path)) {
            assertNotNull(in, path);
            ClassNode node = new ClassNode();
            new ClassReader(in).accept(node, 0);
            return node;
        }
    }

    private static URLClassLoader isolated() {
        URL classes = Probe.class.getProtectionDomain().getCodeSource().getLocation();
        return new URLClassLoader(new URL[] { classes }, null);
    }
}
