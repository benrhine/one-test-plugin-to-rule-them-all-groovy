package com.benrhine.plugins.v1.internal.executer;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.FileTree;
import org.gradle.api.internal.tasks.testing.JvmTestExecutionSpec;
import org.gradle.api.internal.tasks.testing.TestFramework;
import org.gradle.process.JavaForkOptions;
import org.gradle.util.GradleVersion;

import java.lang.reflect.Constructor;
import java.util.Set;

enum JvmTestExecutionSpecFactory {

    FACTORY_FOR_CURRENT_GRADLE_VERSION {
        @Override
        JvmTestExecutionSpec createExecutionSpec(TestFramework testFramework, JvmTestExecutionSpec source) {
            return source.copyWithTestFramework(testFramework);
        }
    },

    FACTORY_FOR_GRADLE_OLDER_THAN_V8 {
        @Override
        JvmTestExecutionSpec createExecutionSpec(TestFramework testFramework, JvmTestExecutionSpec source) {
            try {
                Class<?> clazz = JvmTestExecutionSpec.class;
                // This constructor is available in Gradle 6.4+
                Constructor<?> constructor = clazz.getConstructor(
                    TestFramework.class,
                    Iterable.class,
                    Iterable.class,
                    FileTree.class,
                    boolean.class,
                    FileCollection.class,
                    String.class,
                    org.gradle.util.Path.class,
                    long.class,
                    JavaForkOptions.class,
                    int.class,
                    Set.class
                );

                return (JvmTestExecutionSpec) constructor.newInstance(
                    testFramework,
                    source.getClasspath(),
                    source.getModulePath(),
                    source.getCandidateClassFiles(),
                    source.isScanForTestClasses(),
                    source.getTestClassesDirs(),
                    source.getPath(),
                    source.getIdentityPath(),
                    source.getForkEvery(),
                    source.getJavaForkOptions(),
                    source.getMaxParallelForks(),
                    source.getPreviousFailedTestClasses()
                );
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    },

    FACTORY_FOR_GRADLE_OLDER_THAN_V6_4 {
        @Override
        JvmTestExecutionSpec createExecutionSpec(TestFramework testFramework, JvmTestExecutionSpec source) {
            try {
                Class<?> clazz = JvmTestExecutionSpec.class;
                // This constructor is available in Gradle 4.7+
                Constructor<?> constructor = clazz.getConstructor(
                    TestFramework.class,
                    Iterable.class,
                    FileTree.class,
                    boolean.class,
                    FileCollection.class,
                    String.class,
                    org.gradle.util.Path.class,
                    long.class,
                    JavaForkOptions.class,
                    int.class,
                    Set.class
                );

                return (JvmTestExecutionSpec) constructor.newInstance(
                    testFramework,
                    source.getClasspath(),
                    source.getCandidateClassFiles(),
                    source.isScanForTestClasses(),
                    source.getTestClassesDirs(),
                    source.getPath(),
                    source.getIdentityPath(),
                    source.getForkEvery(),
                    source.getJavaForkOptions(),
                    source.getMaxParallelForks(),
                    source.getPreviousFailedTestClasses()
                );
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }

        }
    };

    abstract JvmTestExecutionSpec createExecutionSpec(TestFramework testFramework, JvmTestExecutionSpec source);

    static JvmTestExecutionSpec testExecutionSpecFor(TestFramework testFramework, JvmTestExecutionSpec source) {
        JvmTestExecutionSpecFactory factory = getInstance();
        return factory.createExecutionSpec(testFramework, source);
    }

    private static JvmTestExecutionSpecFactory getInstance() {
        if (gradleVersionIsAtLeast("8.0")) {
            return FACTORY_FOR_CURRENT_GRADLE_VERSION;
        } else if (gradleVersionIsAtLeast("6.4")) {
            return FACTORY_FOR_GRADLE_OLDER_THAN_V8;
        } else {
            return FACTORY_FOR_GRADLE_OLDER_THAN_V6_4;
        }
    }

    private static boolean gradleVersionIsAtLeast(String version) {
        return GradleVersion.current().getBaseVersion().compareTo(GradleVersion.version(version)) >= 0;
    }

}
