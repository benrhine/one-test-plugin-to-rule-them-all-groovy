package com.benrhine.plugins.v1.internal.util;

import static org.junit.jupiter.api.Assertions.*;

import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * --------------------------------------------------------------------------------------------------------------------
 * SourceSetExtractorTest: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------
 */
public class SourceSetExtractorTest {

    private static Project project;

    @BeforeAll
    static void setupClass() {
        project = ProjectBuilder.builder().build();
    }

    @Disabled
    @Test
    void sourceSets() {
        final SourceSetContainer results = SourceSetExtractor.sourceSets(project);
        // then
        assertNotNull(results);
    }
}
