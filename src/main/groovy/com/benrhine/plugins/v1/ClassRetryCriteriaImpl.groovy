package com.benrhine.plugins.v1

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.SetProperty

/** --------------------------------------------------------------------------------------------------------------------
 * ClassRetryCriteriaImpl: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class ClassRetryCriteriaImpl implements ClassRetryCriteria {

    private final SetProperty<String> includeClasses;
    private final SetProperty<String> includeAnnotationClasses;

    ClassRetryCriteriaImpl(ObjectFactory objects) {
        this.includeClasses = objects.setProperty(String.class);
        this.includeAnnotationClasses = objects.setProperty(String.class);
    }

    @Override
    SetProperty<String> getIncludeClasses() {
        return includeClasses;
    }

    @Override
    SetProperty<String> getIncludeAnnotationClasses() {
        return includeAnnotationClasses;
    }
}
