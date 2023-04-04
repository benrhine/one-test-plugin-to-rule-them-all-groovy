package com.benrhine.plugins.v1.internal.executer.framework

import com.benrhine.plugins.v1.internal.executer.TestFrameworkTemplate
import com.benrhine.plugins.v1.internal.executer.TestNames
import com.benrhine.plugins.v1.internal.testsreader.TestsReader;
import org.gradle.api.internal.tasks.testing.TestFramework;
import org.gradle.api.internal.tasks.testing.junit.JUnitTestFramework;
import org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestFramework;
import org.gradle.api.internal.tasks.testing.testng.TestNGTestFramework;
import org.gradle.util.GradleVersion;

import javax.annotation.Nullable;

/**
 * Instances are scoped to a test task execution and are reused between rounds.
 */
abstract class TestFrameworkStrategy {

    @Nullable
    static TestFrameworkStrategy of(TestFramework testFramework) {
        if (testFramework instanceof JUnitTestFramework) {
            return new JunitTestFrameworkStrategy();
        } else if (testFramework instanceof JUnitPlatformTestFramework) {
            return new Junit5TestFrameworkStrategy();
        } else if (testFramework instanceof TestNGTestFramework) {
            return new TestNgTestFrameworkStrategy();
        } else {
            return null;
        }
    }

//    boolean isLifecycleFailureTest(TestsReader testsReader, String className, String testName);
//
//    TestFramework createRetrying(TestFrameworkTemplate template, TestFramework testFramework, TestNames failedTests);

}
