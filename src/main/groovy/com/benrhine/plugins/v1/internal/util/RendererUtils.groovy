package com.benrhine.plugins.v1.internal.util

import groovy.transform.CompileStatic

/** --------------------------------------------------------------------------------------------------------------------
 * RenderUtils: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
@CompileStatic
class RendererUtils {

    static String escape(String text) {
        text?.replace('[', '\\[')?.replace(']', '\\]')
    }

    static String unescape(String text) {
        text?.replace('\\[', '[')?.replace('\\]', ']')
    }
}