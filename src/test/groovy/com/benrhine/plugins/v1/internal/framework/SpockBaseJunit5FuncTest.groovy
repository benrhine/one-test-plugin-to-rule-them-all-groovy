package com.benrhine.plugins.v1.internal.framework

abstract class SpockBaseJunit5FuncTest extends SpockFuncTest {

    boolean isRerunsParameterizedMethods() {
        false
    }

    @Override
    protected String staticInitErrorTestMethodName(String gradleVersion) {
        gradleVersion == "5.0" ? "classMethod" : "initializationError"
    }

    @Override
    protected String beforeClassErrorTestMethodName(String gradleVersion) {
        "initializationError"
    }

    @Override
    protected String afterClassErrorTestMethodName(String gradleVersion) {
        "executionError"
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
}
