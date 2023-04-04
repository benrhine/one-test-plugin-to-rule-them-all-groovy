package com.benrhine.plugins.v1

import org.gradle.api.Action;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.testing.Test;

/** --------------------------------------------------------------------------------------------------------------------
 * TestRetryTaskExtension: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
/**
 * Allows configuring test retry mechanics.
 * <p>
 * This extension is added with the name 'retry' to all {@link Test} tasks.
 */
interface TestRetryTaskExtension {

    /**
     * The name of the extension added to each test task.
     */
    String NAME = "retry";

    /**
     * Whether tests that initially fail and then pass on retry should fail the task.
     * <p>
     * This setting defaults to {@code false},
     * which results in the task not failing if all tests pass on retry.
     * <p>
     * This setting has no effect if {@link Test#getIgnoreFailures()} is set to true.
     *
     * @return whether tests that initially fails and then pass on retry should fail the task
     */
    Property<Boolean> getFailOnPassedAfterRetry();

    /**
     * The maximum number of times to retry an individual test.
     * <p>
     * This setting defaults to {@code 0}, which results in no retries.
     * Any value less than 1 disables retrying.
     *
     * @return the maximum number of times to retry an individual test
     */
    Property<Integer> getMaxRetries();

    /**
     * The maximum number of test failures that are allowed before retrying is disabled.
     * <p>
     * The count applies to each round of test execution.
     * For example, if maxFailures is 5 and 4 tests initially fail and then 3 again on retry,
     * this will not be considered too many failures and retrying will continue (if maxRetries {@literal >} 1).
     * If 5 or more tests were to fail initially then no retry would be attempted.
     * <p>
     * This setting defaults to {@code 0}, which results in no limit.
     * Any value less than 1 results in no limit.
     *
     * @return the maximum number of test failures that are allowed before retrying is disabled
     */
    Property<Integer> getMaxFailures();

    /**
     * The filter for specifying which tests may be retried.
     */
    Filter getFilter();

    /**
     * The filter for specifying which tests may be retried.
     */
    void filter(Action<? super Filter> action);




    /**
     * The set of criteria specifying which test classes must be retried as a whole unit
     * if retries are enabled and the test class passes the configured {@linkplain TestRetryTaskExtension#getFilter filter}.
     */
    ClassRetryCriteria getClassRetry();

    /**
     * The set of criteria specifying which test classes must be retried as a whole unit
     * if retries are enabled and the test class passes the configured {@linkplain TestRetryTaskExtension#getFilter filter}.
     */
    void classRetry(Action<? super ClassRetryCriteria> action);

}
