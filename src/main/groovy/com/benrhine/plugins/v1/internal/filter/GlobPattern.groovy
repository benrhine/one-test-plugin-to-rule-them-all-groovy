package com.benrhine.plugins.v1.internal.filter

import java.util.regex.Pattern;
import java.util.stream.Collectors;

/** --------------------------------------------------------------------------------------------------------------------
 * GlobPattern: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
final class GlobPattern {

    public static final Pattern STAR_CHAR_PATTERN = Pattern.compile("\\*");
    public static final String MATCH_ANY = ".*?";

    private final String string;
    private final Pattern pattern;

    private GlobPattern(String string, Pattern pattern) {
        this.string = string;
        this.pattern = pattern;
    }

    static GlobPattern from(String string) {
        String patternString = STAR_CHAR_PATTERN.splitAsStream(string)
                .map(Pattern::quote)
                .collect(Collectors.joining(MATCH_ANY));

        if (string.endsWith("*")) {
            patternString = patternString + MATCH_ANY;
        }

        Pattern pattern = Pattern.compile(patternString);

        return new GlobPattern(patternString, pattern);
    }

    boolean matches(String test) {
        return pattern.matcher(test).matches();
    }

    @Override
    public String toString() {
        return string;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GlobPattern that = (GlobPattern) o;

        return string.equals(that.string);
    }

    @Override
    public int hashCode() {
        return string.hashCode();
    }
}