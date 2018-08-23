package com.game

import com.game.diag.ProfilerTool
import java.lang.invoke.SwitchPoint
import java.util.*
import kotlin.concurrent.schedule

internal interface Diagnostic {
    fun enableDiagnostic(enable: Boolean)
    fun setRenderData(time: Float, meshSwitches: Int, materialSwitches: Int)

    fun onGameLogicEnd(startTime: Long, endTime: Long)
    fun onRenderEnd(startTime: Long, endTime: Long)
}

internal class DiagnosticImpl(
        private val profiler: ProfilerTool
): Diagnostic {

    private var diagnosticTimer: TimerTask? = null

    var meshSwitches: Int = 0
    var materialSwitches: Int = 0

    private val updateSamples = mutableListOf<TimeSample>()
    private val renderSamples = mutableListOf<TimeSample>()

    init {
        enableDiagnostic(true)
    }

    private fun printDiagnostic() {
        updateProfiler()
    }

    private fun updateProfiler() {
        if (profiler.isVisible) {
            synchronized(updateSamples) {
                profiler.samplesPanel.updateBuffer(0, updateSamples)
                updateSamples.clear() }
            synchronized(renderSamples) {
                profiler.samplesPanel.updateBuffer(2, renderSamples)
                renderSamples.clear() }
            profiler.samplesPanel.repaint()
        }
    }

    override fun enableDiagnostic(enable: Boolean) {
        if (enable) {
            diagnosticTimer = Timer().schedule(0, 1000){ printDiagnostic() }
        } else {
            diagnosticTimer?.cancel()
        }
    }

    override fun setRenderData(time: Float, meshSwitches: Int, materialSwitches: Int) {
        synchronized(this) {
            this.meshSwitches = meshSwitches
            this.materialSwitches = materialSwitches
        }
    }

    override fun onGameLogicEnd(startTime: Long, endTime: Long) {
        synchronized(updateSamples) {
            updateSamples.add(TimeSample(startTime, endTime)) }
    }

    override fun onRenderEnd(startTime: Long, endTime: Long) {
        synchronized(renderSamples) {
            renderSamples.add(TimeSample(startTime, endTime)) }
    }
}

internal class TimeSample(
        val start: Long,
        val end: Long
)