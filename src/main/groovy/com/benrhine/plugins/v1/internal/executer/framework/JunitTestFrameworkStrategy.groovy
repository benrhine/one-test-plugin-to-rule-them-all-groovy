package com.benrhine.plugins.v1.internal.executer.framework

import static com.benrhine.plugins.v1.internal.util.GradleVersionCheck.gradleVersionIsAtLeast

import com.benrhine.plugins.v1.internal.executer.TestFrameworkTemplate
import com.benrhine.plugins.v1.internal.executer.TestNames;
import org.gradle.api.internal.tasks.testing.TestFramework;
import org.gradle.api.internal.tasks.testing.filter.DefaultTestFilter;
import org.gradle.api.internal.tasks.testing.junit.JUnitTestFramework;
import org.gradle.api.tasks.testing.Test;
import org.gradle.api.tasks.testing.junit.JUnitOptions;

import java.lang.reflect.Constructor;


final class JunitTestFrameworkStrategy extends BaseJunitTestFrameworkStrategy {

    public TestFramework createRetrying(TestFrameworkTemplate template, TestFramework testFramework, TestNames failedTests) {
        DefaultTestFilter failedTestsFilter = testFilterFor(failedTests, true, template);
        return testFrameworkProvider(template, testFramework).testFrameworkFor(failedTestsFilter);
    }

    static class JunitTestFrameworkProvider {

        static class ProviderForGradleOlderThanV8 implements TestFrameworkProvider {

            private final TestFrameworkTemplate template;

            ProviderForGradleOlderThanV8(TestFrameworkTemplate template) {
                this.template = template;
            }

            @Override
            public TestFramework testFrameworkFor(DefaultTestFilter failedTestsFilter) {
                JUnitTestFramework retryTestFramework = newInstance(template.task, failedTestsFilter);
                copyOptions((JUnitOptions) template.task.getTestFramework().getOptions(), retryTestFramework.getOptions());

                return retryTestFramework;
            }

            private static JUnitTestFramework newInstance(Test task, DefaultTestFilter failedTestsFilter) {
                try {
                    Class<?> jUnitTestFrameworkClass = JUnitTestFramework.class;
                    Constructor<?> constructor = jUnitTestFrameworkClass.getConstructor(Test.class, DefaultTestFilter.class);

                    return (JUnitTestFramework) constructor.newInstance(task, failedTestsFilter);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }

            private static void copyOptions(JUnitOptions source, JUnitOptions target) {
                target.setIncludeCategories(source.getIncludeCategories());
                target.setExcludeCategories(source.getExcludeCategories());
            }
        }

        static TestFrameworkProvider testFrameworkProvider(TestFrameworkTemplate template, TestFramework testFramework) {
            if (gradleVersionIsAtLeast("8.0")) {
                return new TestFrameworkProvider.ProviderForCurrentGradleVersion(testFramework);
            } else {
                return new ProviderForGradleOlderThanV8(template);
            }
        }

    }

}
