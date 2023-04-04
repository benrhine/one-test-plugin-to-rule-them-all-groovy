package com.benrhine.plugins.v1

import static com.benrhine.plugins.v1.OneTestPluginToRuleThemAll.SKIP_INTEGRATION_TEST_FLAG_NAME
import static com.benrhine.plugins.v1.OneTestPluginToRuleThemAll.SKIP_TEST_ALL_FLAG_NAME
import static com.benrhine.plugins.v1.OneTestPluginToRuleThemAll.SKIP_TEST_FLAG_NAME

import org.gradle.api.Project

/** --------------------------------------------------------------------------------------------------------------------
 * TestSkippingConditions: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class TestSkippingConditions {

    static Boolean skipTestAll(final Project project) {
        return hasTestAllFlag(project) ||
                (hasSkipTestFlag(project) && hasSkipIntegrationTestFlag(project))
    }

    static Boolean skipTest(final Project project) {
        return hasTestAllFlag(project) || hasSkipTestFlag(project)
    }

    static Boolean skipIntegrationTest(final Project project) {
        return hasTestAllFlag(project) || hasSkipIntegrationTestFlag(project)
    }

    private static Boolean hasTestAllFlag(final Project project) {
        return hasPropertyFlag(project, SKIP_TEST_ALL_FLAG_NAME)
    }

    private static Boolean hasSkipTestFlag(final Project project) {
        return hasPropertyFlag(project, SKIP_TEST_FLAG_NAME)
    }

    private static Boolean hasSkipIntegrationTestFlag(final Project project) {
        return hasPropertyFlag(project, SKIP_INTEGRATION_TEST_FLAG_NAME)
    }

    private static Boolean hasPropertyFlag(final Project project, final String name) {
        if (project.properties.containsKey(name)) {
            final value = project.properties[name]
            return value == null || !value.toString().equals("false", true)
        }
        return false
    }
}
