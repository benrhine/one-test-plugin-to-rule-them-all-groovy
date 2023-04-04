package com.benrhine.plugins.v1.internal.config

/** --------------------------------------------------------------------------------------------------------------------
 * TestRetryTaskExtensionAccessor: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
interface TestRetryTaskExtensionAccessor {

    boolean getFailOnPassedAfterRetry();

    int getMaxRetries();

    int getMaxFailures();

    Set<String> getIncludeClasses();

    Set<String> getIncludeAnnotationClasses();

    Set<String> getExcludeClasses();

    Set<String> getExcludeAnnotationClasses();

    Set<String> getClassRetryIncludeClasses();

    Set<String> getClassRetryIncludeAnnotationClasses();

    boolean getSimulateNotRetryableTest();

}
