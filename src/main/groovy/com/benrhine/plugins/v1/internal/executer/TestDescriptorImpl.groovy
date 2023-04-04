package com.benrhine.plugins.v1.internal.executer;

import org.gradle.api.internal.tasks.testing.TestDescriptorInternal;

import javax.annotation.Nullable;

final class TestDescriptorImpl implements TestDescriptorInternal {

    private final Object syntheticTestId;
    private final TestDescriptorInternal parent;
    private final String testName;

    public TestDescriptorImpl(Object testId, TestDescriptorInternal parent, String testName) {
        this.syntheticTestId = testId;
        this.parent = parent;
        this.testName = testName;
    }

    @Override
    public TestDescriptorInternal getParent() {
        return null;
    }

    @Override
    public Object getId() {
        return syntheticTestId;
    }

    @Nullable
    @Override
    public String getClassName() {
        return parent.getClassName();
    }

    @Override
    public String getClassDisplayName() {
        return parent.getClassDisplayName();
    }

    @Override
    public String getName() {
        return testName;
    }

    @Override
    public String getDisplayName() {
        return testName;
    }

    @Override
    public boolean isComposite() {
        return false;
    }
}
