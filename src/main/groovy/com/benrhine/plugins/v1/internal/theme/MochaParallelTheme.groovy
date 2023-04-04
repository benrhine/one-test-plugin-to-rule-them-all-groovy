package com.benrhine.plugins.v1.internal.theme

import static ThemeType.MOCHA_PARALLEL

import com.benrhine.plugins.v1.TestDescriptorWrapper
import com.benrhine.plugins.v1.TestResultWrapper
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

/** --------------------------------------------------------------------------------------------------------------------
 * MochaParallelTheme: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
@CompileStatic
@InheritConstructors
class MochaParallelTheme extends MochaTheme {

    ThemeType type = MOCHA_PARALLEL

    @Override
    protected String suiteTextInternal(TestDescriptorWrapper descriptor) {
        ''
    }

    @Override
    protected String testTextInternal(TestDescriptorWrapper descriptor, TestResultWrapper result) {
        super.testTextInternal("  [erase-ahead,default]${descriptor.trail} ", descriptor, result)
    }

    @Override
    String exceptionText(TestDescriptorWrapper descriptor, TestResultWrapper result) {
        super.exceptionText(descriptor, result, 4)
    }

    @Override
    String summaryText(TestDescriptorWrapper descriptor, TestResultWrapper result) {
        super.summaryText(descriptor, result, 2)
    }

    @Override
    protected String suiteStandardStreamTextInternal(TestDescriptorWrapper descriptor, String lines) {
        super.standardStreamTextInternal(lines, 4)
    }

    @Override
    protected String testStandardStreamTextInternal(TestDescriptorWrapper descriptor, String lines) {
        super.standardStreamTextInternal(lines, 4)
    }
}