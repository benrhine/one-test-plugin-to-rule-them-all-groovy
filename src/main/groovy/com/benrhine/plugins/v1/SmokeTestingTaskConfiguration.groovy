package com.benrhine.plugins.v1

import org.gradle.api.Project

import static com.benrhine.plugins.v1.OneTestPluginToRuleThemAll.SMOKE_CONFIG_PREFIX
import static com.benrhine.plugins.v1.OneTestPluginToRuleThemAll.SMOKE_TEST_TASK_NAME

/** --------------------------------------------------------------------------------------------------------------------
 * SmokeTestTaskConfiguration: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class SmokeTestingTaskConfiguration extends TestingTaskConfiguration {
    static void apply(final Project project) {
        def sourceSet = setupSourceSet(project, SMOKE_CONFIG_PREFIX)
        setupConfiguration(project, SMOKE_CONFIG_PREFIX)
        setupTestTask(project, sourceSet, SMOKE_CONFIG_PREFIX, SMOKE_TEST_TASK_NAME)
    }
}
