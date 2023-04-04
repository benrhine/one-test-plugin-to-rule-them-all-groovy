package com.benrhine.plugins.v1.internal.executer

import com.benrhine.plugins.v1.internal.config.TestRetryTaskExtensionAccessor
import com.benrhine.plugins.v1.internal.executer.framework.TestFrameworkStrategy
import com.benrhine.plugins.v1.internal.filter.AnnotationInspectorImpl
import com.benrhine.plugins.v1.internal.filter.ClassRetryMatcher
import com.benrhine.plugins.v1.internal.filter.RetryFilter

import static com.benrhine.plugins.v1.internal.executer.JvmTestExecutionSpecFactory.testExecutionSpecFor;

import org.gradle.api.internal.tasks.testing.JvmTestExecutionSpec;
import org.gradle.api.internal.tasks.testing.TestExecuter;
import org.gradle.api.internal.tasks.testing.TestFramework;
import org.gradle.api.internal.tasks.testing.TestResultProcessor;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.testing.Test;
import org.gradle.internal.reflect.Instantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

final class RetryTestExecuter implements TestExecuter<JvmTestExecutionSpec> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RetryTestExecuter.class);
    private final TestRetryTaskExtensionAccessor extension;
    private final TestExecuter<JvmTestExecutionSpec> delegate;
    private final Test testTask;
    private final TestFrameworkTemplate frameworkTemplate;

    private RoundResult lastResult;

    public RetryTestExecuter(
        Test task,
        TestRetryTaskExtensionAccessor extension,
        TestExecuter<JvmTestExecutionSpec> delegate,
        Instantiator instantiator,
        ObjectFactory objectFactory,
        Set<File> testClassesDir,
        Set<File> resolvedClasspath
    ) {
        this.extension = extension;
        this.delegate = delegate;
        this.testTask = task;
        this.frameworkTemplate = new TestFrameworkTemplate(
            testTask,
            instantiator,
            objectFactory,
            testClassesDir,
            resolvedClasspath
        );
    }

    @Override
    public void execute(JvmTestExecutionSpec spec, TestResultProcessor testResultProcessor) {
        int maxRetries = extension.getMaxRetries();
        int maxFailures = extension.getMaxFailures();
        boolean failOnPassedAfterRetry = extension.getFailOnPassedAfterRetry();

        if (maxRetries <= 0) {
            delegate.execute(spec, testResultProcessor);
            return;
        }

        TestFrameworkStrategy testFrameworkStrategy = TestFrameworkStrategy.of(spec.getTestFramework());
        if (testFrameworkStrategy == null) {
            LOGGER.warn("Test retry requested for task {} with unsupported test framework {} - failing tests will not be retried", spec.getIdentityPath(), spec.getTestFramework().getClass().getName());
            delegate.execute(spec, testResultProcessor);
            return;
        }

        AnnotationInspectorImpl annotationInspector = new AnnotationInspectorImpl(frameworkTemplate.testsReader);
        RetryFilter filter = new RetryFilter(
            annotationInspector,
            extension.getIncludeClasses(),
            extension.getIncludeAnnotationClasses(),
            extension.getExcludeClasses(),
            extension.getExcludeAnnotationClasses()
        );

        ClassRetryMatcher classRetryMatcher = new ClassRetryMatcher(
            annotationInspector,
            extension.getClassRetryIncludeClasses(),
            extension.getClassRetryIncludeAnnotationClasses()
        );

        RetryTestResultProcessor retryTestResultProcessor = new RetryTestResultProcessor(
            testFrameworkStrategy,
            filter,
            classRetryMatcher,
            frameworkTemplate.testsReader,
            testResultProcessor,
            maxFailures
        );

        int retryCount = 0;
        JvmTestExecutionSpec testExecutionSpec = spec;

        while (true) {
            delegate.execute(testExecutionSpec, retryTestResultProcessor);
            RoundResult result = retryTestResultProcessor.getResult();
            lastResult = result;

            if (extension.getSimulateNotRetryableTest() || !result.nonRetriedTests.isEmpty()) {
                // fall through to our doLast action to fail accordingly
                testTask.setIgnoreFailures(true);
                break;
            } else if (result.failedTests.isEmpty()) {
                if (retryCount > 0 && !result.hasRetryFilteredFailures && !failOnPassedAfterRetry) {
                    testTask.setIgnoreFailures(true);
                }
                break;
            } else if (result.lastRound) {
                break;
            } else {
                TestFramework retryTestFramework = testFrameworkStrategy.createRetrying(frameworkTemplate, spec.getTestFramework(), result.failedTests);
                testExecutionSpec = testExecutionSpecFor(retryTestFramework, spec);
                retryTestResultProcessor.reset(++retryCount == maxRetries);
            }
        }
    }

    public void failWithNonRetriedTestsIfAny() {
        if (extension.getSimulateNotRetryableTest() || hasNonRetriedTests()) {
            throw new IllegalStateException("The following test methods could not be retried, which is unexpected. Please file a bug report at https://github.com/gradle/test-retry-gradle-plugin/issues" +
                lastResult.nonRetriedTests.stream()
                    .flatMap(entry -> entry.getValue().stream().map(methodName -> "   " + entry.getKey() + "#" + methodName))
                    .collect(Collectors.joining("\n", "\n", "\n")));
        }
    }

    private boolean hasNonRetriedTests() {
        return lastResult != null && !lastResult.nonRetriedTests.isEmpty();
    }

    @Override
    public void stopNow() {
        delegate.stopNow();
    }
}
