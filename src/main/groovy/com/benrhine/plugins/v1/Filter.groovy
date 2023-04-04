package com.benrhine.plugins.v1

import org.gradle.api.provider.SetProperty

/** --------------------------------------------------------------------------------------------------------------------
 * Filter: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
/**
 * A filter for specifying which tests may be retried.
 * <p>
 * By default, all tests are eligible for retrying.
 */
interface Filter {

    /**
     * The patterns used to include tests based on their class name.
     * <p>
     * The pattern string matches against qualified class names.
     * It may contain '*' characters, which match zero or more of any character.
     * <p>
     * A class name only has to match one pattern to be included.
     * <p>
     * If no patterns are specified, all classes (that also meet other configured filters) will be included.
     */
    SetProperty<String> getIncludeClasses();

    /**
     * The patterns used to include tests based on their class level annotations.
     * <p>
     * The pattern string matches against the qualified class names of a test class's annotations.
     * It may contain '*' characters, which match zero or more of any character.
     * <p>
     * A class need only have one annotation matching any of the patterns to be included.
     * <p>
     * Annotations present on super classes that are {@code @Inherited} are considered when inspecting subclasses.
     * <p>
     * If no patterns are specified, all classes (that also meet other configured filters) will be included.
     */
    SetProperty<String> getIncludeAnnotationClasses();

    /**
     * The patterns used to exclude tests based on their class name.
     * <p>
     * The pattern string matches against qualified class names.
     * It may contain '*' characters, which match zero or more of any character.
     * <p>
     * A class name only has to match one pattern to be excluded.
     * <p>
     * If no patterns are specified, all classes (that also meet other configured filters) will be included.
     */
    SetProperty<String> getExcludeClasses();

    /**
     * The patterns used to exclude tests based on their class level annotations.
     * <p>
     * The pattern string matches against the qualified class names of a test class's annotations.
     * It may contain '*' characters, which match zero or more of any character.
     * <p>
     * A class need only have one annotation matching any of the patterns to be excluded.
     * <p>
     * Annotations present on super classes that are {@code @Inherited} are considered when inspecting subclasses.
     * <p>
     * If no patterns are specified, all classes (that also meet other configured filters) will be included.
     */
    SetProperty<String> getExcludeAnnotationClasses();

}