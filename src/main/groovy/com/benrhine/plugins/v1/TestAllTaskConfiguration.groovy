package com.benrhine.plugins.v1

import org.gradle.api.Project

import static com.benrhine.plugins.v1.OneTestPluginToRuleThemAll.TEST_ALL_TASK_NAME
import static com.benrhine.plugins.v1.TestSkippingConditions.skipTestAll

import org.gradle.api.Task
import org.gradle.api.tasks.testing.Test
import org.gradle.language.base.plugins.LifecycleBasePlugin

/** --------------------------------------------------------------------------------------------------------------------
 * TestAllTaskConfiguration: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class TestAllTaskConfiguration {

    static void apply(final Project project) {
        final Task testAllTask = project.tasks.create(TEST_ALL_TASK_NAME)
        testAllTask.description = "Runs all tests."
        testAllTask.group = LifecycleBasePlugin.VERIFICATION_GROUP
        testAllTask.onlyIf { !skipTestAll(project) }
        project.tasks.withType(Test.class).each {
            testAllTask.dependsOn(it.name)
        }
    }
}
