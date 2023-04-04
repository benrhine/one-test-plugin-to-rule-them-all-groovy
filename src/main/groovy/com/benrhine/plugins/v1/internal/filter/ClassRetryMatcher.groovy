package com.benrhine.plugins.v1.internal.filter

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

import java.util.stream.Collectors;

/** --------------------------------------------------------------------------------------------------------------------
 * ClassRetryMatcher: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class ClassRetryMatcher {

    private static final List<String> IMPLICIT_INCLUDE_ANNOTATION_CLASSES = unmodifiableList(asList(
            "spock.lang.Stepwise", // Spock's @Stepwise annotated classes must be retried as a whole
            "com.gradle.enterprise.testing.annotations.ClassRetry" //common testing annotations
    ));

    private final AnnotationInspector annotationInspector;

    private final Set<GlobPattern> includeClasses;
    private final Set<GlobPattern> includeAnnotationClasses;

    ClassRetryMatcher(
            AnnotationInspector annotationInspector,
            Collection<String> includeClasses,
            Collection<String> includeAnnotationClasses
    ) {
        Set<String> mergedIncludeAnnotationClasses = new HashSet<>(IMPLICIT_INCLUDE_ANNOTATION_CLASSES);
        mergedIncludeAnnotationClasses.addAll(includeAnnotationClasses);
        this.annotationInspector = annotationInspector;
        this.includeClasses = toPatterns(includeClasses);
        this.includeAnnotationClasses = toPatterns(mergedIncludeAnnotationClasses);
    }

    boolean retryWholeClass(String className) {
        if (anyMatch(includeClasses, className)) {
            return true;
        }

        Set<String> annotations; // fetching annotations is expensive, don't do it unnecessarily.
        if (!includeAnnotationClasses.isEmpty()) {
            annotations = annotationInspector.getClassAnnotations(className);
            return !annotations.isEmpty() && anyMatch(includeAnnotationClasses, annotations);
        }

        return false;
    }

    private static boolean anyMatch(Set<GlobPattern> patterns, String string) {
        return anyMatch(patterns, Collections.singleton(string));
    }

    private static boolean anyMatch(Set<GlobPattern> patterns, Set<String> strings) {
        return patterns.stream().anyMatch(p -> strings.stream().anyMatch(p::matches));
    }

    private static Set<GlobPattern> toPatterns(Collection<String> strings) {
        return strings.stream().map(GlobPattern::from).collect(Collectors.toSet());
    }
}
