package com.benrhine.plugins.v1.internal.filter

import com.benrhine.plugins.v1.internal.testsreader.TestsReader
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.objectweb.asm.AnnotationVisitor

/** --------------------------------------------------------------------------------------------------------------------
 * AnnotationInspectorImpl: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class AnnotationInspectorImpl implements AnnotationInspector {

    private static final Logger LOGGER = Logging.getLogger(AnnotationInspectorImpl.class);

    private final Map<String, Set<String>> cache = new HashMap<>();
    private final Map<String, Boolean> inheritedCache = new HashMap<>();

    private final TestsReader testsReader;

    AnnotationInspectorImpl(TestsReader testsReader) {
        this.testsReader = testsReader;
    }

    @Override
    Set<String> getClassAnnotations(String className) {
        Set<String> annotations = cache.get(className);
        if (annotations == null) {
            annotations = testsReader.readClass(className, ClassAnnotationVisitor::new)
                    .orElseGet(() -> {
                        LOGGER.warn("Unable to find annotations of " + className);
                        return Collections.emptySet();
                    });
            cache.put(className, annotations);
        }
        return annotations;
    }

    private boolean isInherited(String annotationClassName) {
        return inheritedCache.computeIfAbsent(annotationClassName, ignored ->
                testsReader.readClass(annotationClassName, AnnotationAnnotationVisitor::new)
                        .orElseGet(() -> {
                            LOGGER.warn("Cannot determine whether @" + annotationClassName + " is inherited");
                            return false;
                        })
        );
    }

    final class ClassAnnotationVisitor extends TestsReader.Visitor<Set<String>> {

        private final Set<String> found = new HashSet<>();

        @Override
        Set<String> getResult() {
            return found.isEmpty() ? Collections.emptySet() : found;
        }

        @Override
        void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (!superName.equals("java/lang/Object")) {
                getClassAnnotations(superName.replace('/', '.'))
                        .stream()
                        .filter(AnnotationInspectorImpl::isInherited)
                        .forEach(found::add);
            }
        }

        @Override
        AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            println "ANNOTATION VISITOR $descriptor $visible"
            println "found $found"
            println "something " + descriptor.substring(1, descriptor.length() - 1).replace('/', '.')
//            final String x = classDescriptorToClassName(descriptor);
//            println "THIS IS X $x"
            found.add(descriptor.substring(1, descriptor.length() - 1).replace('/', '.'));
            return null;
        }

    }

    static final class AnnotationAnnotationVisitor extends TestsReader.Visitor<Boolean> {

        private boolean inherited;

        @Override
        public Boolean getResult() {
            return inherited;
        }

        @Override
        AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.equals("Ljava/lang/annotation/Inherited;")) {
                inherited = true;
            }
            return null;
        }
    }

    private static String classDescriptorToClassName(String descriptor) {
        println "IND METHOD $descriptor"
        println descriptor.substring(1, descriptor.length() - 1).replace('/', '.')
        return descriptor.substring(1, descriptor.length() - 1).replace('/', '.')
    }
}