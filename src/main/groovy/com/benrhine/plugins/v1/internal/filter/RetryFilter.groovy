package com.benrhine.plugins.v1.internal.filter

import java.util.stream.Collectors;

/** --------------------------------------------------------------------------------------------------------------------
 * RetryFilter: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class RetryFilter {

    private final AnnotationInspector annotationInspector;

    private final Set<GlobPattern> includeClasses;
    private final Set<GlobPattern> includeAnnotationClasses;
    private final Set<GlobPattern> excludeClasses;
    private final Set<GlobPattern> excludeAnnotationClasses;

    RetryFilter(
            AnnotationInspector annotationInspector,
            Collection<String> includeClasses,
            Collection<String> includeAnnotationClasses,
            Collection<String> excludeClasses,
            Collection<String> excludeAnnotationClasses
    ) {
        this.annotationInspector = annotationInspector;
        this.includeClasses = toPatterns(includeClasses);
        this.includeAnnotationClasses = toPatterns(includeAnnotationClasses);
        this.excludeClasses = toPatterns(excludeClasses);
        this.excludeAnnotationClasses = toPatterns(excludeAnnotationClasses);
    }

    public boolean canRetry(String className) {
        if (!includeClasses.isEmpty()) {
            if (!anyMatch(includeClasses, className)) {
                return false;
            }
        }

        if (anyMatch(excludeClasses, className)) {
            return false;
        }

        Set<String> annotations = null; // fetching annotations is expensive, don't do it unnecessarily.
        if (!includeAnnotationClasses.isEmpty()) {
            annotations = annotationInspector.getClassAnnotations(className);
            if (annotations.isEmpty() || !anyMatch(includeAnnotationClasses, annotations)) {
                return false;
            }
        }

        if (!excludeAnnotationClasses.isEmpty()) {
            annotations = annotations == null ? annotationInspector.getClassAnnotations(className) : annotations;
            return !anyMatch(excludeAnnotationClasses, annotations);
        }

        return true;
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
