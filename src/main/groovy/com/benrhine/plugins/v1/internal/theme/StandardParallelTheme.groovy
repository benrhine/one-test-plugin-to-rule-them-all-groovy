package com.benrhine.plugins.v1.internal.theme

import com.benrhine.plugins.v1.TestDescriptorWrapper
import com.benrhine.plugins.v1.TestResultWrapper
import groovy.transform.CompileStatic
import groovy.transform.InheritConstructors

import static ThemeType.STANDARD_PARALLEL

/** --------------------------------------------------------------------------------------------------------------------
 * StandardParallelTheme: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
@CompileStatic
@InheritConstructors
class StandardParallelTheme extends StandardTheme {

    ThemeType type = STANDARD_PARALLEL

    @Override
    protected String suiteTextInternal(TestDescriptorWrapper descriptor) {
        ''
    }

    @Override
    protected String testTextInternal(TestDescriptorWrapper descriptor, TestResultWrapper result) {
        super.testTextInternal("[erase-ahead,bold]${descriptor.trail}[bold-off] ${descriptor.displayName}", descriptor, result)
    }

    @Override
    String exceptionText(TestDescriptorWrapper descriptor, TestResultWrapper result) {
        super.exceptionText(descriptor, result, 2)
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
