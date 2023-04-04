package com.benrhine.plugins.v1.internal.renderer

import static CharHandlers.HANDLERS

import groovy.transform.CompileStatic

/** --------------------------------------------------------------------------------------------------------------------
 * AnsiTextRenderer: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
@CompileStatic
class AnsiTextRenderer implements TextRenderer {

    @Override
    String render(String input) {
        RenderingContext context = new RenderingContext()

        input.chars.each { char ch ->
            if (HANDLERS.containsKey(ch)) {
                HANDLERS[ch].handle(ch, context)
                return
            }

            if (context.inTag) {
                context.tag << ch
                return
            }

            context << ch
        }

        context
    }
}
