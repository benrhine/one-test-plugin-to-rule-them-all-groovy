package com.benrhine.plugins.v1

import com.benrhine.plugins.v1.internal.util.SourceSetExtractor
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.testing.Test
import org.gradle.language.base.plugins.LifecycleBasePlugin

/** --------------------------------------------------------------------------------------------------------------------
 * TestTaskConfiguration: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class TestingTaskConfiguration {

    protected static SourceSet setupSourceSet(final Project project, final String configPrefix) {
        SourceSetContainer sourceSets = SourceSetExtractor.sourceSets(project)
        SourceSet main = sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
        SourceSet test = sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)

        return sourceSets.create(configPrefix).each {
            it.compileClasspath += test.output + main.output + test.compileClasspath
            it.runtimeClasspath += test.output + main.output + test.runtimeClasspath
        }
    }

    protected static void setupConfiguration(final Project project, final String configPrefix) {
        def list = [
                "testAnnotationProcessor",
                "testCompile",
                "testCompileClasspath",
                "testCompileOnly",
                "testImplementation",
                "testRuntime",
                "testRuntimeClasspath",
                "testRuntimeOnly"
        ]
        list.findAll {
            project.configurations.names.contains(it)
        }.each { setupConfiguration(project, it, configPrefix) }
    }

    protected static void setupConfiguration(final Project project, final String testConfigName, final String configPrefix) {
        def integrationConfigName = testConfigName.replaceFirst("test", configPrefix)

        try {
            final Configuration configuration = project.configurations.getByName(integrationConfigName)

            configuration.extendsFrom(project.configurations.getByName(testConfigName))
            configuration.setVisible(true)
            configuration.setTransitive(true)
        } catch (final Exception e) {
            println e.message
        }
    }

    protected static void setupTestTask(final Project project, final SourceSet sourceSet, final String configPrefix,
                                        final String taskName) {
        def integrationTest = project.tasks.register(taskName, Test.class) {
            it.description = "Runs the $configPrefix tests."
            it.group = LifecycleBasePlugin.VERIFICATION_GROUP
            it.testClassesDirs = sourceSet.output.classesDirs
            it.classpath = sourceSet.runtimeClasspath
            it.mustRunAfter(JavaPlugin.TEST_TASK_NAME)
//            it.onlyIf { !skipIntegrationTest(project) }
            it.testLogging {}
        }
        project.tasks.getByName(JavaBasePlugin.CHECK_TASK_NAME).dependsOn(integrationTest)
    }
}
