package com.benrhine.plugins.v1.internal.testsreader

import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import java.util.function.Supplier;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.objectweb.asm.Opcodes.ASM7;

/** --------------------------------------------------------------------------------------------------------------------
 * TestsReader: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class TestsReader {

    private final Set<File> testClassesDirs;
    private final Iterable<File> classpath;

    TestsReader(Set<File> testClassesDirs, Iterable<File> classpath) {
        this.testClassesDirs = testClassesDirs;
        this.classpath = classpath;
    }

    // Finds classes only within the testClassesDir
    <R> Optional<R> readTestClassDirClass(String className, Supplier<? extends Visitor<R>> factory) {
        return testClassesDirs.stream()
                .map(dir -> new File(dir, classFileName(className)))
                .filter(File::exists)
                .findFirst()
                .map(file -> visitClassFile(file, factory.get()));
    }

    <R> Optional<R> readClass(String className, Supplier<? extends Visitor<R>> factory) {
        Optional<R> opt = readTestClassDirClass(className, factory);
        if (opt.isPresent()) {
            return opt;
        } else {
            return readClasspathClass(className, factory);
        }
    }

    private <R> R visitClassFile(File file, Visitor<R> visitor) {
        try (InputStream inputStream = new FileInputStream(file)) {
            return visit(inputStream, visitor);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private <R> R visit(InputStream inputStream, Visitor<R> visitor) throws IOException {
        ClassReader classReader = new ClassReader(inputStream);
        classReader.accept(visitor, 0);
        return visitor.getResult();
    }
    // Finds classes within the testClassesDir and the rest of the classpath

    private <R> Optional<R> readClasspathClass(String className, Supplier<? extends Visitor<R>> factory) {
        String classFileName = classFileName(className);
        for (File file : classpath) {
            if (!file.exists()) {
                continue;
            }

            if (file.isDirectory()) {
                File classFile = new File(file, classFileName);
                if (classFile.exists()) {
                    return Optional.of(visitClassFile(classFile, factory.get()));
                } else {
                    continue;
                }
            }

            if (!file.getName().endsWith(".jar")) {
                continue;
            }

            try (JarFile jarFile = new JarFile(file)) {
                Optional<JarEntry> classFile = jarFile.stream()
                        .filter(maybeClass -> maybeClass.getName().equals(classFileName))
                        .findAny();

                if (classFile.isPresent()) {
                    try (InputStream is = jarFile.getInputStream(classFile.get())) {
                        return Optional.of(visit(is, factory.get()));
                    }
                }
            } catch (IOException ignored) {
                // we tried... this file looks corrupt, move on to the next jar
            }
        }

        return Optional.empty();
    }

    @NotNull
    private String classFileName(String className) {
        return className.replace('.', '/') + ".class";
    }

    public abstract static class Visitor<T> extends ClassVisitor {

        public Visitor() {
            super(ASM7);
        }

        public abstract T getResult();

    }
}
