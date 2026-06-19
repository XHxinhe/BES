package cn.tesseract.union;

import cn.tesseract.asm.Transformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Preprocessor {
    public static String side = "unknown";
    public static HashMap<String, List<Consumer<ClassNode>>> transformers = new HashMap<>();

    static {
        registerNodeTransformer("net.minecraft.bes.Minecraft", classNode -> {
            MethodNode constructor = new MethodNode(Modifier.PUBLIC, "<init>", "()V", null, null);
            constructor.visitCode();
            constructor.visitVarInsn(Opcodes.ALOAD, 0);
            constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            constructor.visitInsn(Opcodes.RETURN);
            constructor.visitEnd();
            classNode.methods.add(constructor);
        });

        registerNodeTransformer("net.minecraft.bes.World", classNode -> classNode.methods.forEach(method -> Arrays.stream(method.instructions.toArray()).forEach(insn -> {
            if (insn.getOpcode() == Opcodes.INVOKESTATIC) {
                MethodInsnNode methodInsn = (MethodInsnNode) insn;
                if (methodInsn.owner.equals("net/minecraft/bes/Minecraft") && methodInsn.name.equals("getMinecraft")) {
                    method.instructions.set(insn, new InsnNode(Opcodes.ACONST_NULL));
                }
            }
        })));

        registerNodeTransformer("net.minecraft.bes.ServerConfigurationManager", classNode -> classNode.methods.forEach(method -> {
            if (method.name.equals("obf1_b")) Arrays.stream(method.instructions.toArray()).forEach(insn -> {
                if (insn instanceof MethodInsnNode methodInsn) {
                    if (methodInsn.owner.equals("java/io/PrintStream") && methodInsn.name.equals("println") && methodInsn.desc.equals("(Ljava/lang/String;)V")) {
                        method.instructions.set(insn, new InsnNode(Opcodes.POP2));
                    }
                }
            });
        }));

        registerNodeTransformer("net.minecraft.bes.VillageCollection", classNode -> {
            MethodNode constructor = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "(Ljava/lang/String;)V", null, null);
            InsnList insn = constructor.instructions;

            insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insn.add(new VarInsnNode(Opcodes.ALOAD, 1));
            insn.add(new MethodInsnNode(
                    Opcodes.INVOKESPECIAL,
                    "net/minecraft/bes/WorldSavedData",
                    "<init>",
                    "(Ljava/lang/String;)V",
                    false
            ));

            insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insn.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
            insn.add(new InsnNode(Opcodes.DUP));
            insn.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
            insn.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/bes/VillageCollection", "obf1_a", "Ljava/util/List;"));

            insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insn.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
            insn.add(new InsnNode(Opcodes.DUP));
            insn.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
            insn.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/bes/VillageCollection", "obf1_d", "Ljava/util/List;"));

            insn.add(new VarInsnNode(Opcodes.ALOAD, 0));
            insn.add(new TypeInsnNode(Opcodes.NEW, "java/util/ArrayList"));
            insn.add(new InsnNode(Opcodes.DUP));
            insn.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false));
            insn.add(new FieldInsnNode(Opcodes.PUTFIELD, "net/minecraft/bes/VillageCollection", "obf1_b", "Ljava/util/List;"));

            insn.add(new InsnNode(Opcodes.RETURN));

            classNode.methods.add(constructor);
        });
    }

    public static void transform(String className, ClassNode node) {
        node.access = ~(~node.access | Modifier.FINAL | Modifier.PRIVATE | Modifier.PROTECTED) | Modifier.PUBLIC;
        for (MethodNode method : node.methods)
            method.access = ~(~method.access | Modifier.FINAL | Modifier.PRIVATE | Modifier.PROTECTED) | Modifier.PUBLIC;

        if (className.equals("net.minecraft.bes.client.main.Main") || className.equals("net.minecraft.bes.Minecraft") || className.equals("net.minecraft.bes.ServerConfigurationManager"))
            for (FieldNode field : node.fields)
                field.access = ~(~field.access | Modifier.FINAL | Modifier.PRIVATE | Modifier.PROTECTED) | Modifier.PUBLIC;
        else for (FieldNode field : node.fields)
            field.access = ~(~field.access | Modifier.PRIVATE | Modifier.PROTECTED) | Modifier.PUBLIC;
    }

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: <inputJar> <outputJar> [side]");
            System.exit(2);
        }
        Path input = Paths.get(args[0]);
        Path output = Paths.get(args[1]);
        if (args.length > 2) {
            side = args[2];
        }
        if (!Files.exists(input)) {
            System.err.println("Input jar not found: " + input);
            System.exit(3);
        }

        try (JarFile jarFile = new JarFile(input.toFile());
             JarOutputStream jos = new JarOutputStream(new FileOutputStream(output.toFile()))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                InputStream is = jarFile.getInputStream(entry);
                String name = entry.getName();
                JarEntry newEntry = new JarEntry(name);
                jos.putNextEntry(newEntry);
                if (!entry.isDirectory() && name.endsWith(".class")) {
                    String className = name.substring(0, name.length() - 6).replace('/', '.');
                    byte[] classBytes = Transformer.readAllBytes(is);

                    List<Consumer<ClassNode>> transformers = Preprocessor.transformers.get(className);

                    ClassNode classNode = new ClassNode();
                    ClassReader classReader = new ClassReader(classBytes);

                    classReader.accept(classNode, ClassReader.EXPAND_FRAMES);

                    transform(className, classNode);

                    if (transformers != null) {
                        Iterator<Consumer<ClassNode>> it = transformers.iterator();
                        while (it.hasNext()) {
                            it.next().accept(classNode);
                            it.remove();
                        }
                    }

                    ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                    classNode.accept(classWriter);

                    jos.write(classWriter.toByteArray());
                } else {
                    byte[] buf = Transformer.readAllBytes(is);
                    jos.write(buf);
                }
                jos.closeEntry();
                is.close();
            }
        }
    }

    public static void registerNodeTransformer(String className, Consumer<ClassNode> transformer) {
        List<Consumer<ClassNode>> list = transformers.computeIfAbsent(className.replace('/', '.'), k -> new ArrayList<>());
        list.add(transformer);
    }
}
