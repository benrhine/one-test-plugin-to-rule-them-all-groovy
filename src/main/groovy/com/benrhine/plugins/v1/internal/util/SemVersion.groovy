package com.benrhine.plugins.v1.internal.util
/** --------------------------------------------------------------------------------------------------------------------
 * SemVersion: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class SemVersion implements Comparable<SemVersion> {

    Integer major
    Integer minor
    Integer patch

    SemVersion(final Integer major, final Integer minor, final Integer patch) {
        this.major = major
        this.minor = minor
        this.patch = patch
    }

    int compareTo(final SemVersion other) {
        return 0 //comparator.compare(this, other) // TODO
    }

    protected static Boolean comparator = Comparator.comparingInt { it.major }
            .thenComparingInt { it.minor }
            .thenComparingInt { it.patch }

    static SemVersion parse(final String version) {
        return parseOrNull(version)
    }

    static SemVersion parseOrNull(final String version) {
        if (version == null || version.isEmpty()) {
            throw new IllegalArgumentException("Expected semantic version. Got\"$version\"")
        }
//        def chunkWithNumbers = version.removePrefix("v").split("-").first()
//        def numbers = chunkWithNumbers.split(".").toTypedArray().map { Integer.parseInt(it) }
        final List<Integer> tmp = new ArrayList<>()
        final numbers = version.split("\\.")

        for (final String str : numbers) {
            tmp.add(Integer.parseInt(str))
        }

        if (tmp.isEmpty()) {
            throw new IllegalArgumentException("Expected semantic version. Got\"$version\"")
        }
        Integer major
        Integer minor
        Integer patch

        try {
            major = tmp.get(0) ?: 0
        } catch (final Exception e) {
            major = 0
        }

        try {
            minor = tmp.get(1) ?: 0
        } catch (final Exception e) {
            minor = 0
        }

        try {
            patch = tmp.get(2) ?: 0
        } catch (final Exception e) {
            patch = 0
        }
        return new SemVersion(major, minor, patch)
    }
}
