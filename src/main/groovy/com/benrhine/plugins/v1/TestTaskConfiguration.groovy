package com.benrhine.plugins.v1

import static com.benrhine.plugins.v1.TestSkippingConditions.skipTest

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

/** --------------------------------------------------------------------------------------------------------------------
 * TestTaskConfiguration: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class TestTaskConfiguration {

    static void apply(final Project project) {
        final Task testTask = project.tasks.getByName(JavaPlugin.TEST_TASK_NAME)
        testTask.onlyIf { !skipTest(project) }
    }
}
