package com.benrhine.plugins.v1.internal.framework

class JUnit4ViaJUnitVintageFuncTest extends JUnit4FuncTest {

    @Override
    protected isRerunsAllParameterizedIterations() {
        true
    }

    @Override
    protected String initializationErrorSyntheticTestMethodName(String gradleVersion) {
        gradleVersion == "5.0" ? "classMethod" : "initializationError"
    }

    @Override
    protected String afterClassErrorTestMethodName(String gradleVersion) {
        gradleVersion == "5.0" ? "classMethod" : "executionError"
    }

    @Override
    protected String beforeClassErrorTestMethodName(String gradleVersion) {
        gradleVersion == "5.0" ? "classMethod" : "initializationError"
    }

    protected String buildConfiguration() {
        return '''
            dependencies {
                testImplementation "junit:junit:4.13.2"
                testImplementation "org.junit.jupiter:junit-jupiter-api:5.9.2"
                testRuntimeOnly "org.junit.vintage:junit-vintage-engine:5.9.2"
            }

            test {
                useJUnitPlatform()
            }
        '''
    }
}
