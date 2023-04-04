package com.benrhine.plugins.v1.internal.framework

import org.gradle.util.GradleVersion

class Spock2FuncTest extends SpockBaseJunit5FuncTest {

    @Override
    boolean canTargetInheritedMethods(String gradleVersion) {
        GradleVersion.version(gradleVersion) >= GradleVersion.version("7.0")
    }

    @Override
    protected String beforeClassErrorTestMethodName(String gradleVersion) {
        gradleVersion == "5.0" ? "classMethod" : "initializationError"
    }

    @Override
    protected String afterClassErrorTestMethodName(String gradleVersion) {
        gradleVersion == "5.0" ? "classMethod" : "executionError"
    }

    @Override
    protected String buildConfiguration() {
        return """
            dependencies {
                implementation 'org.spockframework:spock-core:2.3-groovy-3.0'
            }
            test {
                useJUnitPlatform()
            }
        """
    }

    @Override
    protected String contextualTestExtension() {
        """
            package acme

            import org.spockframework.runtime.extension.AbstractAnnotationDrivenExtension
            import org.spockframework.runtime.model.SpecInfo

            class ContextualTestExtension extends AbstractAnnotationDrivenExtension<ContextualTest> {

                @Override
                void visitSpecAnnotation(ContextualTest annotation, SpecInfo spec) {

                    spec.features.each { feature ->
                        feature.reportIterations = true
                        if (feature.parameterized) {
                            def currentNameProvider = feature.iterationNameProvider
                            feature.iterationNameProvider = {
                                def defaultName = currentNameProvider != null ? currentNameProvider.getName(it) : feature.name
                                defaultName + " [suffix]"
                            }
                        } else {
                            feature.name = feature.name + " [suffix]"
                        }
                    }
                }
            }
        """
    }
}
