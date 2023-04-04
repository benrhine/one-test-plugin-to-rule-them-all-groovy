package com.benrhine.plugins.v1.internal.theme

import static ThemeType.PLAIN_PARALLEL

import com.benrhine.plugins.v1.TestDescriptorWrapper
import com.benrhine.plugins.v1.TestResultWrapper
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

/** --------------------------------------------------------------------------------------------------------------------
 * PlainParallelTheme: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
@CompileStatic
@InheritConstructors
class PlainParallelTheme extends PlainTheme {

    ThemeType type = PLAIN_PARALLEL

    @Override
    protected String suiteTextInternal(TestDescriptorWrapper descriptor) {
        ''
    }

    @Override
    protected String testTextInternal(TestDescriptorWrapper descriptor, TestResultWrapper result) {
        super.testTextInternal("${descriptor.trail} ${descriptor.displayName} ${RESULT_TYPE_MAPPING[result.resultType]}", descriptor, result)
    }

    @Override
    protected String exceptionText(TestDescriptorWrapper descriptor, TestResultWrapper result, int indent) {
        return super.exceptionText(descriptor, result, 2)
    }

    @Override
    protected String suiteStandardStreamTextInternal(TestDescriptorWrapper descriptor, String lines) {
        super.standardStreamTextInternal(lines, 2)
    }

    @Override
    protected String testStandardStreamTextInternal(TestDescriptorWrapper descriptor, String lines) {
        super.standardStreamTextInternal(lines, 2)
    }
}