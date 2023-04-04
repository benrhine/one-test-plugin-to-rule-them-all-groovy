package com.benrhine.plugins.v1.internal.executer

import com.benrhine.plugins.v1.internal.executer.framework.TestFrameworkStrategy
import com.benrhine.plugins.v1.internal.filter.ClassRetryMatcher
import com.benrhine.plugins.v1.internal.filter.RetryFilter
import com.benrhine.plugins.v1.internal.testsreader.TestsReader
import org.gradle.api.internal.tasks.testing.TestCompleteEvent
import org.gradle.api.internal.tasks.testing.TestDescriptorInternal
import org.gradle.api.internal.tasks.testing.TestResultProcessor
import org.gradle.api.internal.tasks.testing.TestStartEvent
import org.gradle.api.tasks.testing.TestFailure;
import org.gradle.api.tasks.testing.TestOutputEvent

import java.lang.reflect.Method

import static org.gradle.api.tasks.testing.TestResult.ResultType.SKIPPED;

final class RetryTestResultProcessor implements TestResultProcessor {

    private final TestFrameworkStrategy testFrameworkStrategy;
    private final RetryFilter filter;
    private final ClassRetryMatcher classRetryMatcher;
    private final TestsReader testsReader;
    private final TestResultProcessor delegate;

    private final int maxFailures;
    private boolean lastRetry;
    private boolean hasRetryFilteredFailures;
    private Method failureMethod;

    private final Map<Object, TestDescriptorInternal> activeDescriptorsById = new HashMap<>();

    private TestNames currentRoundFailedTests = new TestNames();
    private TestNames previousRoundFailedTests = new TestNames();

    private Object rootTestDescriptorId;

    RetryTestResultProcessor(TestFrameworkStrategy testFrameworkStrategy, RetryFilter filter, ClassRetryMatcher classRetryMatcher,
        TestsReader testsReader, TestResultProcessor delegate, int maxFailures) {
        this.testFrameworkStrategy = testFrameworkStrategy;
        this.filter = filter;
        this.classRetryMatcher = classRetryMatcher;
        this.testsReader = testsReader;
        this.delegate = delegate;
        this.maxFailures = maxFailures;
    }

    @Override
    void started(TestDescriptorInternal descriptor, TestStartEvent testStartEvent) {
        if (rootTestDescriptorId == null) {
            rootTestDescriptorId = descriptor.getId();
            activeDescriptorsById.put(descriptor.getId(), descriptor);
            delegate.started(descriptor, testStartEvent);
        } else if (!descriptor.getId().equals(rootTestDescriptorId)) {
            activeDescriptorsById.put(descriptor.getId(), descriptor);
            delegate.started(descriptor, testStartEvent);
        }
    }

    @Override
    void completed(Object testId, TestCompleteEvent testCompleteEvent) {
        if (testId.equals(rootTestDescriptorId)) {
            if (!lastRun()) {
                return;
            }
        } else {
            TestDescriptorInternal descriptor = activeDescriptorsById.remove(testId);
            if (descriptor != null && descriptor.getClassName() != null) {
                String className = descriptor.getClassName();
                String name = descriptor.getName();

                boolean failedInPreviousRound = previousRoundFailedTests.remove(className, name);
                if (failedInPreviousRound && testCompleteEvent.getResultType() == SKIPPED) {
                    addRetry(className, name);
                }

                // class-level lifecycle failures do not guarantee that all methods that failed in the previous round will be re-executed (e.g. due to class setup failure)
                // in this case, we retry the entire class, so we ignore method-level failures for the next round
                // we keep all lifecycle failures from previous round to make sure we report them as passed later on
                if (isLifecycleFailure(className, name)) {
                    previousRoundFailedTests.remove(className, n -> {
                        if (isLifecycleFailure(className, n)) {
                            addRetry(className, n);
                        }
                        return true;
                    });
                }

                if (isClassDescriptor(descriptor)) {
                    previousRoundFailedTests.remove(className, n -> {
                        if (isLifecycleFailure(className, n)) {
                            emitFakePassedEvent(descriptor, testCompleteEvent, n);
                            return true;
                        } else {
                            return false;
                        }
                    });
                }

            }
        }

        delegate.completed(testId, testCompleteEvent);
    }

    private boolean isLifecycleFailure(String className, String name) {
        return testFrameworkStrategy.isLifecycleFailureTest(testsReader, className, name);
    }

    private void addRetry(String className, String name) {
        println "classnamne $className"
        println "name $name"
        if (classRetryMatcher.retryWholeClass(className)) {
            currentRoundFailedTests.addClass(className);
        } else {
            currentRoundFailedTests.add(className, name);
        }
    }

    private void emitFakePassedEvent(TestDescriptorInternal parent, TestCompleteEvent parentEvent, String name) {
        Object syntheticTestId = new Object();
        TestDescriptorInternal syntheticDescriptor = new TestDescriptorImpl(syntheticTestId, parent, name);
        long timestamp = parentEvent.getEndTime();
        delegate.started(syntheticDescriptor, new TestStartEvent(timestamp, parent.getId()));
        delegate.completed(syntheticTestId, new TestCompleteEvent(timestamp));
    }

    private boolean isClassDescriptor(TestDescriptorInternal descriptor) {
        return descriptor.getClassName() != null && descriptor.getClassName().equals(descriptor.getName());
    }

    @Override
    void output(Object testId, TestOutputEvent testOutputEvent) {
        delegate.output(testId, testOutputEvent);
    }

    @SuppressWarnings("unused")
    void failure(Object testId, Throwable throwable) {
        // Gradle 7.6 changed the method signature from failure(Object, Throwable) to failure(Object, TestFailure).
        // To maintain compatibility with older versions, the original method needs to exist and needs to call failure()
        // on the delegate via reflection.
        failure(testId);
        try {
            Method failureMethod = lookupFailureMethod();
            failureMethod.invoke(delegate, testId, throwable);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Method lookupFailureMethod() throws ReflectiveOperationException {
        if (failureMethod == null) {
            failureMethod = delegate.getClass().getMethod("failure", Object.class, Throwable.class);
        }
        return failureMethod;
    }

    @Override
    void failure(Object testId, TestFailure result) {
        println "OMG $testId"
        println "OMG 1 $result"
        failure(testId)
        delegate.failure(testId, result)
    }

    protected void failure(Object testId) {
        final TestDescriptorInternal descriptor = activeDescriptorsById.get(testId);
        if (descriptor != null) {
            String className = descriptor.getClassName();
            if (className != null) {
                if (filter.canRetry(className)) {
                    addRetry(className, descriptor.getName());
                } else {
                    hasRetryFilteredFailures = true;
                }
            }
        }
    }

    private boolean lastRun() {
        return currentRoundFailedTests.isEmpty()
            || lastRetry
            || currentRoundFailedTestsExceedsMaxFailures();
    }

    private boolean currentRoundFailedTestsExceedsMaxFailures() {
        return maxFailures > 0 && currentRoundFailedTests.size() >= maxFailures;
    }

    RoundResult getResult() {
        return new RoundResult(currentRoundFailedTests, previousRoundFailedTests, lastRun(), hasRetryFilteredFailures);
    }

    void reset(boolean lastRetry) {
        if (lastRun()) {
            throw new IllegalStateException("processor has completed");
        }

        this.lastRetry = lastRetry;
        this.previousRoundFailedTests = currentRoundFailedTests;
        this.currentRoundFailedTests = new TestNames();
        this.activeDescriptorsById.clear();
    }

}
