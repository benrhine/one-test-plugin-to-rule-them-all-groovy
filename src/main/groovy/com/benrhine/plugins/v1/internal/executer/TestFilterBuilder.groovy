package com.benrhine.plugins.v1.internal.executer;

import org.gradle.api.internal.tasks.testing.filter.DefaultTestFilter;

final class TestFilterBuilder {

    private final DefaultTestFilter filter = new DefaultTestFilter();

    public void test(String className, String methodName) {
        filter.includeTest(className, methodName);
    }

    public void clazz(String className) {
        filter.includeTestsMatching(className); // don't use includeTest with null method - it doesn't work < Gradle 6
    }

    public DefaultTestFilter build() {
        return filter;
    }
}
