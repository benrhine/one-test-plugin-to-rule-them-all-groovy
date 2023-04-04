package com.benrhine.plugins.v1.internal.util

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.tasks.SourceSetContainer

/** --------------------------------------------------------------------------------------------------------------------
 * SourceSetExtractor: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class SourceSetExtractor {
    private static SemVersion GRADLE_VERSION_WITH_EXTENSION = SemVersion.parse("7.1.0")

    static SourceSetContainer sourceSets(final Project project) {
//        return project.extensions.getByType(JavaPluginExtension.class).sourceSets
        if (usesExtension(project)) {
            return project.extensions.getByType(JavaPluginExtension.class).sourceSets
        } else {
            return project.extensions.getPlugin(JavaPluginConvention.class).sourceSets
        }
    }

    private static Boolean usesExtension(final Project project)  {
        def gradleVersion = SemVersion.parseOrNull(project.gradle.gradleVersion)
                ?: GRADLE_VERSION_WITH_EXTENSION
        return gradleVersion >= GRADLE_VERSION_WITH_EXTENSION
    }
}
