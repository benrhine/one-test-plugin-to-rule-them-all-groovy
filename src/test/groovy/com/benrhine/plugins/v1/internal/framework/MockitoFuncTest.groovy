package com.benrhine.plugins.v1.internal.framework

import com.benrhine.plugins.v1.AbstractFrameworkFuncTest

class MockitoFuncTest extends AbstractFrameworkFuncTest {

    @Override
    protected String buildConfiguration() {
        super.buildConfiguration() + """
            dependencies {
                testImplementation("org.mockito:mockito-core:3.11.2")
            }
        """
    }

    def "retries on unnecessary stubbings"() {
        given:
        buildFile << """
            test.retry.maxRetries = 1
        """

        writeTestSource """
            package acme;

            import org.junit.*;
            import org.junit.runner.*;
            import org.mockito.*;
            import org.mockito.junit.*;


            import static org.mockito.Mockito.*;

            @RunWith(MockitoJUnitRunner.class)
            public class TestWithUnnecessaryStubbings {
                @Mock
                CharSequence s;

                @Before
                public void setup() {
                  when(s.length()).thenReturn(3);
                }

                @Test
                public void someTest() {
                }
            }
        """

        when:
        def result = gradleRunner(gradleVersion).buildAndFail()

        then:
        with(result.output) {
            it.count('acme.TestWithUnnecessaryStubbings > unnecessary Mockito stubbings FAILED') == 2
            !it.contains("unable to retry the following test methods, which is unexpected.")
        }

        where:
        gradleVersion << GRADLE_VERSIONS_UNDER_TEST
    }
}
