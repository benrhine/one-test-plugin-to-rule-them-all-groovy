package com.benrhine.plugins.v1.internal.executer.framework

import com.benrhine.plugins.v1.internal.testsreader.TestsReader;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.ASM7;

/**
 * Class visitor that identifies unparameterized test method names.
 */
final class SpockParameterClassVisitor extends TestsReader.Visitor<Map<String, List<String>>> {

    // A valid Java identifier https://docs.oracle.com/javase/specs/jls/se8/html/jls-3.html#jls-3.8 including methods
    private static final String SPOCK_PARAM_PATTERN = '#[\\p{L}\\d$_.()&&[^#\\s]]+';
    private static final String WILDCARD = ".*";

    private final Set<String> failedTestNames;
    private final TestsReader testsReader;
    private final SpockParameterMethodVisitor spockMethodVisitor = new SpockParameterMethodVisitor();
    private boolean isSpec;

    public SpockParameterClassVisitor(Set<String> testMethodName, TestsReader testsReader) {
        this.failedTestNames = testMethodName;
        this.testsReader = testsReader;
    }

    @Override
    public Map<String, List<String>> getResult() {
        if (!isSpec) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> map = new HashMap<>();
        spockMethodVisitor.annotationVisitor.testMethodPatterns.forEach(
            methodPattern -> {
                // Replace params in the method name with .*
                String methodPatternRegex = Arrays.stream(methodPattern.split(SPOCK_PARAM_PATTERN))
                    .map(Pattern::quote)
                    .collect(Collectors.joining(WILDCARD))
                    + WILDCARD; // For when no params in name - [iterationNum] implicitly added to end

                failedTestNames.forEach(failedTestName -> {
                    List<String> matches = map.computeIfAbsent(failedTestName, ignored -> new ArrayList<>());
                    if (methodPattern.equals(failedTestName) || failedTestName.matches(methodPatternRegex)) {
                        matches.add(methodPattern);
                    }
                });
            });
        return map;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        if (superName != null) {
            if (superName.equals("spock/lang/Specification")) {
                isSpec = true;
            } else if (!superName.equals("java/lang/Object")) {
                testsReader.readClass(superName.replace('/', '.'), () -> this);
            }
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return isSpec ? spockMethodVisitor : null;
    }

    private static final class SpockParameterMethodVisitor extends MethodVisitor {

        private final SpockFeatureMetadataAnnotationVisitor annotationVisitor = new SpockFeatureMetadataAnnotationVisitor();

        public SpockParameterMethodVisitor() {
            super(ASM7);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            if (descriptor.contains("org/spockframework/runtime/model/FeatureMetadata")) {
                return annotationVisitor;
            }
            return null;
        }

        /**
         * Looking for signatures like:
         * org/spockframework/runtime/model/FeatureMetadata;(
         * line=15,
         * name="unrolled with param #param",
         * ordinal=0,
         * blocks={...},
         * parameterNames={"param", "result"}
         * )
         */
        private static final class SpockFeatureMetadataAnnotationVisitor extends AnnotationVisitor {

            private final List<String> testMethodPatterns = new ArrayList<>();

            public SpockFeatureMetadataAnnotationVisitor() {
                super(ASM7);
            }

            @Override
            public void visit(String name, Object value) {
                if ("name".equals(name)) {
                    testMethodPatterns.add((String) value);
                }
            }

        }

    }

}

