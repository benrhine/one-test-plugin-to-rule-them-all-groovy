package com.benrhine.plugins.v1

import static com.benrhine.plugins.v1.OneTestPluginToRuleThemAll.INTEGRATION_CONFIG_PREFIX
import static com.benrhine.plugins.v1.OneTestPluginToRuleThemAll.INTEGRATION_TEST_TASK_NAME

import org.gradle.api.Project;

/** --------------------------------------------------------------------------------------------------------------------
 * IntegrationTestTaskConfiguration: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class IntegrationTestingTaskConfiguration extends TestingTaskConfiguration {
    static void apply(final Project project) {
        def sourceSet = setupSourceSet(project, INTEGRATION_CONFIG_PREFIX)
        setupConfiguration(project, INTEGRATION_CONFIG_PREFIX)
        setupTestTask(project, sourceSet, INTEGRATION_CONFIG_PREFIX, INTEGRATION_TEST_TASK_NAME)
    }


}
