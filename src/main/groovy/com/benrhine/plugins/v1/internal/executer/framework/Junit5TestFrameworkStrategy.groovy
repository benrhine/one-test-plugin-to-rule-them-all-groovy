package com.benrhine.plugins.v1.internal.executer.framework

import static com.benrhine.plugins.v1.internal.util.GradleVersionCheck.gradleVersionIsAtLeast

import com.benrhine.plugins.v1.internal.executer.TestFrameworkTemplate
import com.benrhine.plugins.v1.internal.executer.TestNames;
import org.gradle.api.internal.tasks.testing.TestFramework;
import org.gradle.api.internal.tasks.testing.filter.DefaultTestFilter;
import org.gradle.api.internal.tasks.testing.junitplatform.JUnitPlatformTestFramework;
import org.gradle.api.tasks.testing.junitplatform.JUnitPlatformOptions

import java.lang.reflect.Constructor;

final class Junit5TestFrameworkStrategy extends BaseJunitTestFrameworkStrategy {

    TestFramework createRetrying(TestFrameworkTemplate template, TestFramework testFramework, TestNames failedTests) {
        DefaultTestFilter failedTestsFilter = testFilterFor(failedTests, false, template);
        return Junit5TestFrameworkProvider.testFrameworkProvider(template, testFramework).testFrameworkFor(failedTestsFilter);
    }

    static class Junit5TestFrameworkProvider {

        static class ProviderForGradleOlderThanV8 implements TestFrameworkProvider {

            private final TestFrameworkTemplate template;

            ProviderForGradleOlderThanV8(TestFrameworkTemplate template) {
                this.template = template;
            }

            @Override
            public TestFramework testFrameworkFor(DefaultTestFilter failedTestsFilter) {
                JUnitPlatformTestFramework retryTestFramework = newInstance(failedTestsFilter);
                copyOptions((JUnitPlatformOptions) template.task.getTestFramework().getOptions(), retryTestFramework.getOptions());

                return retryTestFramework;
            }

            private static JUnitPlatformTestFramework newInstance(DefaultTestFilter failedTestsFilter) {
                try {
                    Class<?> jUnitPlatformTestFrameworkClass = JUnitPlatformTestFramework.class;
                    Constructor<?> constructor = jUnitPlatformTestFrameworkClass.getConstructor(DefaultTestFilter.class);

                    return (JUnitPlatformTestFramework) constructor.newInstance(failedTestsFilter);
                } catch (ReflectiveOperationException e) {
                    throw new RuntimeException(e);
                }
            }

            private static void copyOptions(JUnitPlatformOptions source, JUnitPlatformOptions target) {
                target.setIncludeEngines(source.getIncludeEngines());
                target.setExcludeEngines(source.getExcludeEngines());
                target.setIncludeTags(source.getIncludeTags());
                target.setExcludeTags(source.getExcludeTags());
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
