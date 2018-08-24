package com.game.diag

import java.util.*
import kotlin.concurrent.schedule

internal interface Diagnostic {
    fun enableDiagnostic(enable: Boolean)
    fun setRenderData(time: Float, meshSwitches: Int, materialSwitches: Int)

    fun onUpdateEnd(startTime: Long, endTime: Long)
    fun onSortEnd(startTime: Long, endTime: Long)
    fun onRenderEnd(startTime: Long, endTime: Long)
}

internal class DiagnosticImpl(
        private val profiler: ProfilerTool
): Diagnostic {

    private var diagnosticTimer: TimerTask? = null

    var meshSwitches: Int = 0
    var materialSwitches: Int = 0

    private val updateSamples = mutableListOf<TimeSample>()
    private val sortSamples = mutableListOf<TimeSample>()
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
            synchronized(sortSamples) {
                profiler.samplesPanel.updateBuffer(1, sortSamples)
                sortSamples.clear() }
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

    override fun onUpdateEnd(startTime: Long, endTime: Long) {
        synchronized(updateSamples) {
            updateSamples.add(TimeSample(startTime, endTime)) }
    }

    override fun onSortEnd(startTime: Long, endTime: Long) {
        synchronized(sortSamples) {
            sortSamples.add(TimeSample(startTime, endTime)) }
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