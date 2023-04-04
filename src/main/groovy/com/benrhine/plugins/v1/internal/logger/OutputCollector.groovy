package com.benrhine.plugins.v1.internal.logger

import com.benrhine.plugins.v1.TestDescriptorWrapper
import groovy.transform.CompileStatic

/** --------------------------------------------------------------------------------------------------------------------
 * OutputCollector: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */

@CompileStatic
class OutputCollector {

    private static final Closure mappingFunction = {
        new StringBuilder()
    }

    private final Map<String, StringBuilder> collector = [:]

    void collect(TestDescriptorWrapper descriptor, String output) {
        collector.computeIfAbsent(descriptor.id, mappingFunction) << output
    }

    String pop(TestDescriptorWrapper descriptor) {
        def output = collector.computeIfAbsent(descriptor.id, mappingFunction).toString()
        collector.remove(descriptor.id).length = 0
        output
    }
}
