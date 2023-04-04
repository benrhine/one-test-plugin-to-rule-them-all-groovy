package com.benrhine.plugins.v1.internal.framework

class SpockViaJUnitVintageFuncTest extends SpockBaseJunit5FuncTest {

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
                implementation "org.codehaus.groovy:groovy:2.5.8"
                testImplementation "org.spockframework:spock-core:1.3-groovy-2.5"
                testImplementation "org.junit.jupiter:junit-jupiter-api:5.9.2"
                testRuntimeOnly "org.junit.vintage:junit-vintage-engine:5.9.2"
            }

            test {
                useJUnitPlatform()
            }
        """.stripIndent()
    }
}
