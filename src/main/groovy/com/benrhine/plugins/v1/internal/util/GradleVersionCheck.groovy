package com.benrhine.plugins.v1.internal.util

import org.gradle.util.GradleVersion

/** --------------------------------------------------------------------------------------------------------------------
 * GradleVersionCheck: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
final class GradleVersionCheck {

    static boolean gradleVersionIsAtLeast(String version) {
        return GradleVersion.current().getBaseVersion().compareTo(GradleVersion.version(version)) >= 0
    }
}
