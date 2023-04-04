package com.benrhine.plugins.v1.internal.logger

import com.benrhine.plugins.v1.TestLoggerExtension
import com.benrhine.plugins.v1.internal.theme.Theme
import com.benrhine.plugins.v1.internal.theme.ThemeFactory
import groovy.transform.CompileStatic
import org.gradle.StartParameter
import org.gradle.api.tasks.testing.Test

/** --------------------------------------------------------------------------------------------------------------------
 * TestLoggerWrapper: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
@CompileStatic
class TestLoggerWrapper implements TestLogger {

    private final StartParameter startParameter
    private final Test test
    private final TestLoggerExtension testLoggerExtension

    private TestLogger testLoggerDelegate

    TestLoggerWrapper(StartParameter startParameter, Test test, TestLoggerExtension testLoggerExtension) {
        this.startParameter = startParameter
        this.test = test
        this.testLoggerExtension = testLoggerExtension
    }

    @Delegate
    TestLogger getTestLoggerDelegate() {
        if (testLoggerDelegate) {
            return testLoggerDelegate
        }

        Theme theme = ThemeFactory.getTheme(startParameter, test, testLoggerExtension)

        if (theme.type.parallel) {
            testLoggerDelegate = new ParallelTestLogger(test.logger, testLoggerExtension, theme)
        } else {
            testLoggerDelegate = new SequentialTestLogger(test.logger, testLoggerExtension, theme)
        }

        return testLoggerDelegate
    }
}
