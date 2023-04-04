package com.benrhine.plugins.v1.functional


import org.gradle.testkit.runner.GradleRunner

/** --------------------------------------------------------------------------------------------------------------------
 * OneTestPluginToRuleThemAllSpec: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class OneTestPluginToRuleThemAllSpec extends AbstractFunctionalSpec {
// https://dev.to/autonomousapps/gradle-all-the-way-down-testing-your-gradle-plugin-with-gradle-testkit-2hmc
    // AbstractProject implements AutoCloseable
//    @AutoCleanup
//    AbstractProject project

    File buildFile

    final setup() {
        buildFile = new File(testProjectDir, 'build.gradle')
        buildFile << """
            plugins {
                id 'com.benrhine.one-test-plugin-to-rule-them-all-groovy'
            }
        """
    }

    final "successfully created or verify the testIntegration directories exists"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withPluginClasspath()
                .build()

        then:
            final File intSrc = new File("$testProjectDir.path/$INTEGRATION_ROOT")
            true == intSrc.exists()
    }

    final "successfully created or verify the testSmoke directories exists"() {
        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir)
                .withPluginClasspath()
                .build()

        then:
        final File smokeSrc = new File("$testProjectDir.path/$SMOKE_ROOT")
        true == smokeSrc.exists()
    }

}
