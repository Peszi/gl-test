package com.game

import java.util.*
import kotlin.concurrent.schedule

internal class Diagnostic {

    private var diagnosticTimer =
            Timer().schedule(0, 1000){ printDiagnostic() }

    private var framesCount = 0
    private var frameStartTime = 0L

    private var updatesCount = 0
    private var updateStartTime = 0L

    var updateTime = 0f
    var renderTime = 0f

    var sortingTime = 0f

    private fun printDiagnostic() {
        val diagnosticMessage = "fps $framesCount updates $updatesCount " +
                        "frame ${renderTime.format(2)}/${EngineCore.TARGET_FRAME_TIME.format(2)}ms " +
                        "update: ${updateTime.format(2)}ms "
        if (renderTime <= EngineCore.TARGET_FRAME_TIME) println(diagnosticMessage) else System.err.println(diagnosticMessage)
        updatesCount = 0
        framesCount = 0
    }

    fun beginUpdate() { updateStartTime = System.nanoTime() }

    fun endUpdate() { updateTime = getTime(updateStartTime); updatesCount++ }

    fun beginFrame() { frameStartTime = System.nanoTime() }

    fun endFrame() { renderTime = getTime(frameStartTime); framesCount++ }

    companion object {

        fun Float.format(digits: Int) =
                java.lang.String.format("%.${digits}f", this)

        fun getTime(startTime: Long): Float =
                (System.nanoTime() - startTime) / 1000_000f
    }
}