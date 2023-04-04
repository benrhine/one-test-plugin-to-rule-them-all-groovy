package com.benrhine.plugins.v1.internal.util


import spock.lang.Specification
import spock.lang.Unroll

/** --------------------------------------------------------------------------------------------------------------------
 * RendererUtilsTest: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */

class RendererUtilsSpec extends Specification {

    @Unroll
    def "escape #text"() {
        expect:
        RendererUtils.escape(text) == expected
        where:
        text         | expected
        null         | null
        ''           | ''
        '[escape]'   | '\\[escape\\]'
        '\u001Btext' | '\u001Btext'
    }

    @Unroll
    def "unescape #text"() {
        expect:
        RendererUtils.unescape(text) == expected
        where:
        text                 | expected
        null                 | null
        ''                   | ''
        '[red]\\[escaped\\]' | '[red][escaped]'
        '\u001B[0mANSI'      | '\u001B[0mANSI'
    }
}