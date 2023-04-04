package com.benrhine.plugins.v1.internal.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * --------------------------------------------------------------------------------------------------------------------
 * SemVersionTest: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------
 */
class SemVersionTest {

    private SemVersion version;

    @BeforeEach
    void setUp() {
        version = new SemVersion(0, 0, 0);
    }


    @Test
    void defaultConstructor() {
        assertNotNull(version);
    }

    @Test
    void compareTo() {
    }

    @Test
    void parse() {
        // When
        final SemVersion version = SemVersion.parse("1.2.3");
        // Then
        assertEquals(1, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(3, version.getPatch());
    }

    @Test
    void parseOrNullWhenNull() {
        // When
        final Exception result = assertThrows(IllegalArgumentException.class, () ->
                SemVersion.parseOrNull(null)
        );
        // Then
        assertTrue(result.getMessage().contains("Expected semantic version"));
    }

    @Test
    void parseOrNullWhenEmpty() {
        // When
        final Exception result = assertThrows(IllegalArgumentException.class, () ->
                SemVersion.parseOrNull("")
        );
        // Then
        assertTrue(result.getMessage().contains("Expected semantic version"));
    }

    @Test
    void parseOrNull() {
        // When
        final SemVersion version = SemVersion.parseOrNull("1.2.3");
        // Then
        assertEquals(1, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(3, version.getPatch());
    }

    @Test
    void major() {
        // Given
        assertEquals(0, version.getMajor());
        // When
        version.setMajor(1);
        // Then
        assertEquals(1, version.getMajor());
    }


    @Test
    void minor() {
        // Given
        assertEquals(0, version.getMinor());
        // When
        version.setMinor(1);
        // Then
        assertEquals(1, version.getMinor());
    }

    @Test
    void patch() {
        // Given
        assertEquals(0, version.getPatch());
        // When
        version.setPatch(1);
        // Then
        assertEquals(1, version.getPatch());
    }
}