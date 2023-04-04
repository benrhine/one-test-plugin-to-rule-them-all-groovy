package com.benrhine.plugins.v1

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty

/** --------------------------------------------------------------------------------------------------------------------
 * FilterImpl: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class FilterImpl implements Filter {

    private final SetProperty<String> includeClasses;
    private final SetProperty<String> includeAnnotationClasses;
    private final SetProperty<String> excludeClasses;
    private final SetProperty<String> excludeAnnotationClasses;

    FilterImpl(ObjectFactory objects) {
        this.includeClasses = objects.setProperty(String.class);
        this.includeAnnotationClasses = objects.setProperty(String.class);
        this.excludeClasses = objects.setProperty(String.class);
        this.excludeAnnotationClasses = objects.setProperty(String.class);
    }

    @Override
    public SetProperty<String> getIncludeClasses() {
        return includeClasses;
    }

    @Override
    public SetProperty<String> getIncludeAnnotationClasses() {
        return includeAnnotationClasses;
    }

    @Override
    public SetProperty<String> getExcludeClasses() {
        return excludeClasses;
    }

    @Override
    public SetProperty<String> getExcludeAnnotationClasses() {
        return excludeAnnotationClasses;
    }
}
