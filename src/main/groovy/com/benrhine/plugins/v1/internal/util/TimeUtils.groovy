package com.benrhine.plugins.v1.internal.util

import groovy.transform.CompileStatic

/** --------------------------------------------------------------------------------------------------------------------
 * TimeUtils: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */

@CompileStatic
final class TimeUtils {

    private static final int HOUR = 3600 * 1000
    private static final int MINUTE = 60 * 1000
    private static final int SECOND = 1000

    static String humanDuration(long millis) {
        def duration = [:]

        if (millis >= HOUR) {
            duration.h = millis / HOUR as int
            duration.m = (millis / MINUTE as int) % 60
        } else if (millis >= MINUTE) {
            duration.m = millis / MINUTE as int
            duration.s = (millis / SECOND as int) % 60
        } else if (millis >= SECOND) {
            duration.s = (millis / SECOND * 10 as int) / 10
        } else if (millis > 0) {
            duration.ms = millis
        } else {
            duration.ms = '0'
        }

        duration.entrySet().findAll {
            it.value
        }.collect {
            "${it.value}${it.key}"
        }.join(' ')
    }
}