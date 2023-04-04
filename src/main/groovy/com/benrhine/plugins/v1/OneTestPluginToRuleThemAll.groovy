package com.benrhine.plugins.v1

import com.benrhine.plugins.v1.internal.config.DefaultTestRetryTaskExtension

import static com.benrhine.plugins.v1.internal.config.TestTaskConfigurer.configureTestTask

import com.benrhine.plugins.v1.internal.logger.TestLoggerWrapper
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.testing.Test

import javax.inject.Inject

/** --------------------------------------------------------------------------------------------------------------------
 * SlackAlertsPlugin: Main plugin class.
 * ------------------------------------------------------------------------------------------------------------------ */

class OneTestPluginToRuleThemAll implements Plugin<Project> {
    private final ObjectFactory objectFactory;
    private final ProviderFactory providerFactory;
    // Declared DSL block titles
    private static final String TEST_CONFIG  = "testConfig"
    private static final String TEST_LOGGER = 'testLogger'
    private static final String SLACK_MESSAGES = "slackMessages"
    private static final String PLUGIN_ID = "com.benrhine.integration-test";
    private static final String INTEGRATION_CONFIG_PREFIX = "testIntegration";
    private static final String INTEGRATION_TEST_TASK_NAME = "integrationTest";
    private static final String SMOKE_CONFIG_PREFIX = "testSmoke";
    private static final String SMOKE_TEST_TASK_NAME = "smokeTest";
    private static final String TEST_ALL_TASK_NAME = "testAll";
    private static final String SKIP_TEST_FLAG_NAME = "skipUnitTests";
    private static final String SKIP_TEST_ALL_FLAG_NAME = "skipTests";
    private static final String SKIP_INTEGRATION_TEST_FLAG_NAME = "skipIntegrationTests";

    @Inject
    OneTestPluginToRuleThemAll(final ObjectFactory objectFactory, final ProviderFactory providerFactory) {
        this.objectFactory = objectFactory;
        this.providerFactory = providerFactory;
    }

    /**
     * apply: Applies plugin to the implementing project.
     *
     * @param project
     */
    @Override
    void apply(final Project project) {
        if (pluginAlreadyApplied(project)) {
            return;
        }
        // Get the project source directory
        final String srcDir = "$project.projectDir/src"
        // Declare the plugin config DSL
        final config = project.extensions.create(TEST_CONFIG, OneTestPluginToRuleThemAllExtension)
        final configLogger = project.extensions.create(TEST_LOGGER, TestLoggerExtension, project)

        project.getTasks()
                .withType(Test.class)
                .configureEach(task -> configureTestTask(task, objectFactory, providerFactory));

        project.gradle.afterProject {
            if (!project.plugins.hasPlugin(JavaPlugin.class)) {
                project.plugins.apply(JavaPlugin.class);
            }
            String integrationPath, smokePath, smokeAuthenticated, smokeUnauthenticated, smokeValidation

            if (config.allowFullPath) {
                integrationPath = "$config.integrationPath"
                smokePath = "$config.smokePath"
            } else {
                if (config.integrationPath && config.topPackageName) {
                    integrationPath = "$srcDir/$config.integrationPath/java/$config.topPackageName"
                } else if (!config.integrationPath && config.topPackageName) {
                    integrationPath = "$srcDir/$INTEGRATION_CONFIG_PREFIX/java/$config.topPackageName"
                } else if (config.integrationPath && !config.topPackageName) {
                    integrationPath = "$srcDir/$config.integrationPath/java"
                } else {
                    integrationPath = "$srcDir/$INTEGRATION_CONFIG_PREFIX/java"
                }
                if (config.smokePath && config.topPackageName) {
                    smokePath = "$srcDir/$config.smokePath/java/$config.topPackageName"
                } else if (!config.smokePath && config.topPackageName) {
                    smokePath = "$srcDir/$SMOKE_CONFIG_PREFIX/java/$config.topPackageName"
                } else if (config.smokePath && !config.topPackageName) {
                    smokePath = "$srcDir/$config.smokePath/java"
                } else {
                    smokePath = "$srcDir/$SMOKE_CONFIG_PREFIX/java"
                }
            }
            verifyOrCreatePath(integrationPath)
            verifyOrCreatePath(smokePath)

            if (config.includeAuthenticated && config.topPackageName) {
                smokeAuthenticated = "$smokePath/$config.topPackageName/authenticated"
                verifyOrCreatePath(smokeAuthenticated)
            } else {
                println "Package purposefully excluded or package name not set"
            }

            if (config.includeUnauthenticated && config.topPackageName) {
                smokeUnauthenticated = "$smokePath/$config.topPackageName/unauthenticated"
                verifyOrCreatePath(smokeUnauthenticated)
            } else {
                println "Package purposefully excluded or package name not set"
            }

            if (config.includeValidation && config.topPackageName) {
                smokeValidation = "$smokePath/$config.topPackageName/validation"
                verifyOrCreatePath(smokeValidation)
            } else {
                println "Package purposefully excluded or package name not set"
            }
            setupPlugin(project)

            project.tasks.withType(Test).configureEach { Test test ->
                // Configure Retry
//                test.extensions.create("retry", DefaultTestRetryTaskExtension, project, test)
//                configureTestTask(test, this.objectFactory, this.providerFactory)
                // Configure Logging
                def testExtension = test.extensions.create(TEST_LOGGER, TestLoggerExtension, project, test)

                testExtension.originalTestLoggingEvents = test.testLogging.events
                test.testLogging.lifecycle.events = []



                def testLogger = new TestLoggerWrapper(project.gradle.startParameter, test, testExtension)

                test.addTestListener(testLogger)
                test.addTestOutputListener(testLogger)
            }
        }
    }

    private static boolean pluginAlreadyApplied(Project project) {
        return project.getPlugins().stream().anyMatch(plugin -> plugin.getClass().getName().equals(OneTestPluginToRuleThemAll.class.getName()));
    }

    private void setupPlugin(final Project project) {
        IntegrationTestingTaskConfiguration.apply(project)
        SmokeTestingTaskConfiguration.apply(project)
        TestTaskConfiguration.apply(project)
        TestAllTaskConfiguration.apply(project)
    }

    private void verifyOrCreatePath(final String fullPath) {
        final String[] splitPath = fullPath.split("/")
        String path = ""
        boolean firstIteration = true
        for (final String str : splitPath) {
            if (firstIteration) {
                path = path + str
                firstIteration = false
            } else {
                path = path + "/" + str
            }

            final File src = new File(path)

            if (src.isDirectory() && src.canWrite()) {
                if (!src.exists()) {
                    boolean result = src.mkdir()
                }
            } else {
                if (!src.exists()) {
                    boolean result = src.mkdir()

                }
            }
        }
    }

}