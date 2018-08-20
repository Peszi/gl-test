package com.game

import java.util.*
import kotlin.concurrent.schedule

internal interface Diagnostic {
    fun enableDiagnostic(enable: Boolean)
}

internal class DiagnosticImpl: Diagnostic {

    private var diagnosticTimer =
            Timer().schedule(0, 1000){ printDiagnostic() }

    @Volatile private var framesCount = 0
    @Volatile private var updatesCount = 0

    @Volatile var updateTime = 0f
    @Volatile var renderTime = 0f
    @Volatile var sortTime = 0f

    private fun printDiagnostic() {
        val diagnosticMessage = "fps $framesCount updates $updatesCount " +
                        "frame ${renderTime.format(2)}/${EngineCore.TARGET_FRAME_TIME.format(2)}ms " +
                        "update: ${updateTime.format(2)}ms " +
                        "sort: ${sortTime.format(2)}ms "
        if (renderTime <= EngineCore.TARGET_FRAME_TIME) println(diagnosticMessage) else System.err.println(diagnosticMessage)
        updatesCount = 0
        framesCount = 0
    }

    override fun enableDiagnostic(enable: Boolean) {
        if (enable) {
            diagnosticTimer = Timer().schedule(0, 1000){ printDiagnostic() }
        } else {
            diagnosticTimer.run()
            diagnosticTimer.cancel()
        }
    }

    companion object {

        fun Float.format(digits: Int) =
                java.lang.String.format("%.${digits}f", this)

    }
}