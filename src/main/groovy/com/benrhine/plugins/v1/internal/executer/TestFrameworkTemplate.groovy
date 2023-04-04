package com.benrhine.plugins.v1.internal.executer

import com.benrhine.plugins.v1.internal.testsreader.TestsReader
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.reflect.Instantiator

class TestFrameworkTemplate {

    public final Test task;
    public final Instantiator instantiator;
    public final ObjectFactory objectFactory;
    public final TestsReader testsReader;

    public TestFrameworkTemplate(Test task, Instantiator instantiator, ObjectFactory objectFactory, Set<File> testClassesDir, Set<File> resolvedClasspath) {
        this.task = task;
        this.instantiator = instantiator;
        this.objectFactory = objectFactory;
        this.testsReader = new TestsReader(testClassesDir, resolvedClasspath);
    }

    public TestFilterBuilder filterBuilder() {
        return new TestFilterBuilder();
    }
}
