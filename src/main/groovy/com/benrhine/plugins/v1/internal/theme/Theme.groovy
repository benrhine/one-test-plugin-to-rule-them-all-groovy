package com.benrhine.plugins.v1.internal.theme

import com.benrhine.plugins.v1.TestDescriptorWrapper
import com.benrhine.plugins.v1.TestResultWrapper

/** --------------------------------------------------------------------------------------------------------------------
 * Theme: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
interface Theme {

    ThemeType getType()

    String suiteText(TestDescriptorWrapper descriptor, TestResultWrapper result)

    String testText(TestDescriptorWrapper descriptor, TestResultWrapper result)

    String exceptionText(TestDescriptorWrapper descriptor, TestResultWrapper result)

    String summaryText(TestDescriptorWrapper descriptor, TestResultWrapper result)

    String suiteStandardStreamText(TestDescriptorWrapper descriptor, String lines, TestResultWrapper result)

    String testStandardStreamText(TestDescriptorWrapper descriptor, String lines, TestResultWrapper result)
}
