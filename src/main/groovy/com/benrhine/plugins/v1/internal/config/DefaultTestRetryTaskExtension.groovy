package com.benrhine.plugins.v1.internal.config

import com.benrhine.plugins.v1.ClassRetryCriteria
import com.benrhine.plugins.v1.ClassRetryCriteriaImpl
import com.benrhine.plugins.v1.Filter
import com.benrhine.plugins.v1.FilterImpl
import com.benrhine.plugins.v1.TestRetryTaskExtension
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.SetProperty;

import javax.inject.Inject;

/** --------------------------------------------------------------------------------------------------------------------
 * DefaultTestRetryTaskExtension: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class DefaultTestRetryTaskExtension implements TestRetryTaskExtension {

    private final Property<Boolean> failOnPassedAfterRetry;
    private final Property<Integer> maxRetries;
    private final Property<Integer> maxFailures;
    private final Filter filter;

    private final ClassRetryCriteria classRetryCriteria;

    @Inject
    DefaultTestRetryTaskExtension(ObjectFactory objects) {
        this.failOnPassedAfterRetry = objects.property(Boolean.class);
        this.maxRetries = objects.property(Integer.class);
        this.maxFailures = objects.property(Integer.class);
        this.filter = new FilterImpl(objects);
        this.classRetryCriteria = new ClassRetryCriteriaImpl(objects);
    }

    Property<Boolean> getFailOnPassedAfterRetry() {
        return failOnPassedAfterRetry;
    }

    Property<Integer> getMaxRetries() {
        return maxRetries;
    }

    Property<Integer> getMaxFailures() {
        return maxFailures;
    }

    void filter(Action<? super Filter> action) {
        action.execute(filter);
    }

    Filter getFilter() {
        return filter;
    }

    ClassRetryCriteria getClassRetry() {
        return classRetryCriteria;
    }

    void classRetry(Action<? super ClassRetryCriteria> action) {
        action.execute(classRetryCriteria);
    }

}
