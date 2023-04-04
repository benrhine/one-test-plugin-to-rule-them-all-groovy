package com.benrhine.plugins.v1.internal.executer.framework

import com.benrhine.plugins.v1.internal.executer.TestFilterBuilder
import com.benrhine.plugins.v1.internal.executer.TestFrameworkTemplate
import com.benrhine.plugins.v1.internal.executer.TestNames
import com.benrhine.plugins.v1.internal.testsreader.TestsReader;
import org.gradle.api.internal.tasks.testing.filter.DefaultTestFilter
import org.slf4j.Logger;
import org.slf4j.LoggerFactory

abstract class BaseJunitTestFrameworkStrategy extends TestFrameworkStrategy {

    public static final Logger LOGGER = LoggerFactory.getLogger(JunitTestFrameworkStrategy.class);
    static final Set<String> ERROR_SYNTHETIC_TEST_NAMES = Collections.unmodifiableSet(
        new HashSet<>(Arrays.asList(
            "classMethod",
            "executionError",
            "initializationError",
            "unnecessary Mockito stubbings"
        ))
    );

    boolean isLifecycleFailureTest(TestsReader testsReader, String className, String testName) {
        return ERROR_SYNTHETIC_TEST_NAMES.contains(testName);
    }

    protected DefaultTestFilter testFilterFor(TestNames failedTests, boolean canRunParameterizedSpockMethods, TestFrameworkTemplate template) {
        TestFilterBuilder filter = template.filterBuilder();
        addFilters(filter, template.testsReader, failedTests, canRunParameterizedSpockMethods);

        return filter.build();
    }

    protected void addFilters(TestFilterBuilder filters, TestsReader testsReader, TestNames failedTests, boolean canRunParameterizedSpockMethods) {
        failedTests.stream()
            .forEach(entry -> {
                String className = entry.getKey();
                Set<String> tests = entry.getValue();

                if (tests.isEmpty()) {
                    filters.clazz(className);
                    return;
                }

                if (tests.stream().anyMatch(ERROR_SYNTHETIC_TEST_NAMES::contains)) {
                    filters.clazz(className);
                    return;
                }

                Boolean something = null

                try {
                    Optional<Map<String, List<String>>> resultOpt = testsReader.readTestClassDirClass(className, () -> new SpockParameterClassVisitor(tests, testsReader));
                    if (resultOpt.isPresent()) {
                        Map<String, List<String>> result = resultOpt.get();
                        if (result.isEmpty()) {
                            something = false; // not a spec
                        }

                        if (something == null) {

                            if (canRunParameterizedSpockMethods) {
                                result.forEach((test, matches) -> {
                                    if (matches.isEmpty()) {
                                        addPotentiallyParameterizedSuffixed(filters, className, test);
                                    } else {
                                        matches.forEach(match -> filters.test(className, match));
                                    }
                                });
                            } else {
                                boolean allLiteralMethodMatches = result.entrySet()
                                        .stream()
                                        .allMatch(e2 -> e2.getValue().size() == 1 && e2.getValue().get(0).equals(e2.getKey()));

                                if (allLiteralMethodMatches) {
                                    tests.forEach(test -> filters.test(className, test));
                                } else {
                                    filters.clazz(className);
                                }
                            }

                            something = true;
                        }
                    }
                } catch (Throwable t) {
                    LOGGER.warn("Unable to determine if class " + className + " contains Spock @Unroll parameterizations", t)
                    something = false;
                }

                if (something) {
                    return;
                }
                // TODO no idea why it WILL NOT call these methods claiming the param types are wrong
                // ive verified the param types and do not see the issue
//                if (processSpockTest(filters, testsReader, canRunParameterizedSpockMethods, className, tests)) {
//                    return;
//                }

//                tests.each {name -> addPotentiallyParameterizedSuffixed(filters, className, name) };

                tests.each {
                    String strippedParameterName = it.replaceAll('(?:\\([^)]*?\\)|\\[[^]]*?])*$', "");
                    filters.test(className, strippedParameterName);
                    filters.test(className, it);
                }
            });
    }

    private boolean processSpockTest(TestFilterBuilder filters, TestsReader testsReader, boolean canRunParameterizedSpockMethods, String className, Set<String> tests) {
        println "DID I GET IN THE METHOD 1"
        try {
            Optional<Map<String, List<String>>> resultOpt = testsReader.readTestClassDirClass(className, () -> new SpockParameterClassVisitor(tests, testsReader));
            if (resultOpt.isPresent()) {
                println "DID I GET IN THE METHOD 2"
                Map<String, List<String>> result = resultOpt.get();
                if (result.isEmpty()) {
                    println "DID I GET IN THE METHOD 3"
                    return false; // not a spec
                }

                if (canRunParameterizedSpockMethods) {
                    println "DID I GET IN THE METHOD 4"
                    result.forEach((test, matches) -> {
                        if (matches.isEmpty()) {
                            addPotentiallyParameterizedSuffixed(filters, className, test);
                        } else {
                            matches.forEach(match -> filters.test(className, match));
                        }
                    });
                } else {
                    println "DID I GET IN THE METHOD 5"
                    boolean allLiteralMethodMatches = result.entrySet()
                        .stream()
                        .allMatch(e2 -> e2.getValue().size() == 1 && e2.getValue().get(0).equals(e2.getKey()));

                    if (allLiteralMethodMatches) {
                        tests.forEach(test -> filters.test(className, test));
                    } else {
                        filters.clazz(className);
                    }
                }

                return true;
            }
        } catch (Throwable t) {
            LOGGER.warn("Unable to determine if class " + className + " contains Spock @Unroll parameterizations", t);
        }
        return false;
    }

    private void addPotentiallyParameterizedSuffixed(TestFilterBuilder filters, String className, String name) {
        // It's a common pattern to add all the parameters on the end of a literal method name with []
        String strippedParameterName = name.replaceAll('(?:\\([^)]*?\\)|\\[[^]]*?])*$', "");
        filters.test(className, strippedParameterName);
        filters.test(className, name);
    }
}
