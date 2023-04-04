package com.benrhine.plugins.v1.base

import org.gradle.api.Project
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner

/** --------------------------------------------------------------------------------------------------------------------
 * TestProjectRunner: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class TestProjectRunner {

    BuildResult runGradle(final Project project, final List<String> args, final String gradleVersion) {
        def builder = GradleRunner.create()
                .withProjectDir(project.projectDir)
                .withArguments(args)
                .withPluginClasspath()
                .forwardOutput()
        if (gradleVersion != null && !gradleVersion.isEmpty() && gradleVersion != "current") {
            builder.withGradleVersion(gradleVersion)
        }
        return builder.build()
    }
}
