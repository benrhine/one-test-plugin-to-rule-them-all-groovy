package com.benrhine.plugins.v1.internal.theme


import org.gradle.api.GradleException
import spock.lang.Specification
import spock.lang.Unroll


class ThemeTypeSpec extends Specification {

    @Unroll
    def "fromName can construct ThemeType from name #name"() {
        expect:
        ThemeType.fromName(name) == themeType
        where:
            name                | themeType
            'plain'             | ThemeType.PLAIN
            'plain-parallel'    | ThemeType.PLAIN_PARALLEL
            'standard'          | ThemeType.STANDARD
            'standard-parallel' | ThemeType.STANDARD_PARALLEL
            'mocha'             | ThemeType.MOCHA
            'mocha-parallel'    | ThemeType.MOCHA_PARALLEL
    }

    def "fromName throws exception if unknown theme name is specified"() {
        when:
            ThemeType.fromName('unknown')
        then:
            thrown(GradleException)
    }

    def "get all theme names"() {
        expect:
            ThemeType.allThemeNames == "'plain', 'plain-parallel', 'standard', 'standard-parallel', 'mocha', 'mocha-parallel'"
    }

    def "get parallel theme names"() {
        expect:
            ThemeType.parallelThemeNames == "'plain-parallel', 'standard-parallel', 'mocha-parallel'"
    }

    @Unroll
    def "instance of #themeType theme has the correct type set"() {
        expect:
            themeType.themeClass.newInstance(null).type == themeType
        where:
            themeType << ThemeType.values().toList()
    }
}
