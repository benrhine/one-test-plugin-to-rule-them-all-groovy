package com.benrhine.plugins.v1.internal.logger

import static com.benrhine.plugins.v1.internal.util.RendererUtils.unescape

import com.benrhine.plugins.v1.internal.renderer.AnsiTextRenderer
import com.benrhine.plugins.v1.internal.renderer.TextRenderer
import groovy.transform.CompileStatic
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger

/** --------------------------------------------------------------------------------------------------------------------
 * ConsoleLogger: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */


@CompileStatic
class ConsoleLogger {

    private final Logger logger
    private final LogLevel level
    private final TextRenderer renderer

    ConsoleLogger(Logger logger, LogLevel level, TextRenderer renderer = new AnsiTextRenderer()) {
        this.logger = logger
        this.level = level
        this.renderer = renderer
    }

    void log(String text) {
        if (text) {
            logger.log(level, unescape(renderer.render(text)))
        }
    }

    void logNewLine() {
        logger.log(level, '')
    }
}