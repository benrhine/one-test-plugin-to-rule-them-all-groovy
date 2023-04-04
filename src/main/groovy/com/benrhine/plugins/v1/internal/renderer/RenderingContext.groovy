package com.benrhine.plugins.v1.internal.renderer

import static org.fusesource.jansi.Ansi.Erase.*
import static org.fusesource.jansi.Ansi.ansi

import groovy.transform.CompileStatic
import org.fusesource.jansi.Ansi

/** --------------------------------------------------------------------------------------------------------------------
 * RenderingContext: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
@CompileStatic
class RenderingContext implements Appendable {

    //@formatter:off
    private static final Map<String, Closure<Ansi>> TAG_MAPPING = [
            'bold'              : { Ansi ansi -> ansi.bold() },
            'bold-off'          : { Ansi ansi -> ansi.boldOff() },
            'default'           : { Ansi ansi -> ansi.fgDefault() },
            'grey'              : { Ansi ansi -> ansi.fgBrightBlack()},
            'red'               : { Ansi ansi -> ansi.fgRed() },
            'bright-red'        : { Ansi ansi -> ansi.fgBrightRed() },
            'green'             : { Ansi ansi -> ansi.fgGreen() },
            'bright-green'      : { Ansi ansi -> ansi.fgBrightGreen() },
            'yellow'            : { Ansi ansi -> ansi.fgYellow() },
            'bright-yellow'     : { Ansi ansi -> ansi.fgBrightYellow() },
            'cyan'              : { Ansi ansi -> ansi.fgCyan() },
            'bright-cyan'       : { Ansi ansi -> ansi.fgBrightCyan() },
            'blue'              : { Ansi ansi -> ansi.fgBlue() },
            'bright-blue'       : { Ansi ansi -> ansi.fgBrightBlue() },
            'magenta'           : { Ansi ansi -> ansi.fgMagenta() },
            'bright-magenta'    : { Ansi ansi -> ansi.fgBrightMagenta() },
            'cursor-up-line'    : { Ansi ansi -> ansi.cursorUpLine() },
            'erase-all'         : { Ansi ansi -> ansi.eraseLine(ALL) },
            'erase-ahead'       : { Ansi ansi -> ansi.eraseLine(FORWARD) },
            'erase-back'        : { Ansi ansi -> ansi.eraseLine(BACKWARD) },
            '/'                 : { Ansi ansi -> ansi.reset() }
    ]
    // @formatter:on

    private final Ansi ansi
    StringBuilder tag
    boolean inTag
    boolean escaped

    RenderingContext() {
        ansi = ansi()
        tag = new StringBuilder()
    }

    void beginTag() {
        inTag = true
    }

    void endTag() {
        def tags = tag.toString().split(',')
        tags.each { String tag ->
            if (TAG_MAPPING.containsKey(tag)) {
                def mapping = TAG_MAPPING[tag]

                mapping.call(ansi)
            } else {
                ansi.a("[${tag}]")
            }
        }

        this.tag = new StringBuilder()
        inTag = false
    }

    @Override
    RenderingContext append(CharSequence csq) {
        ansi.a(csq)
        this
    }

    @Override
    RenderingContext append(CharSequence csq, int start, int end) {
        ansi.a(csq, start, end)
        this
    }

    @Override
    RenderingContext append(char c) {
        ansi.a(c)
        this
    }

    @Override
    String toString() {
        ansi.toString()
    }
}