package com.benrhine.plugins.v1.internal.executer.framework;

import org.gradle.api.internal.tasks.testing.TestFramework;
import org.gradle.api.internal.tasks.testing.filter.DefaultTestFilter;

interface TestFrameworkProvider {

    TestFramework testFrameworkFor(DefaultTestFilter failedTestsFilter);

    class ProviderForCurrentGradleVersion implements TestFrameworkProvider {

        private final TestFramework testFramework;

        ProviderForCurrentGradleVersion(TestFramework testFramework) {
            this.testFramework = testFramework;
        }

        @Override
        public TestFramework testFrameworkFor(DefaultTestFilter failedTestsFilter) {
            return testFramework.copyWithFilters(failedTestsFilter);
        }
    }

}
