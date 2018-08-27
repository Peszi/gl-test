package com.game.diag

import java.util.*
import kotlin.concurrent.schedule

internal interface Diagnostic {
    fun enableDiagnostic(enable: Boolean)
    fun setRenderData(time: Float, meshSwitches: Int, materialSwitches: Int)

    fun onUpdateEnd(sample: TimeSample)
    fun onSortEnd(sample: TimeSample)
    fun onRenderEnd(sample: TimeSample)
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

    override fun onUpdateEnd(sample: TimeSample) {
        synchronized(updateSamples) {
            updateSamples.add(sample) }
    }

    override fun onSortEnd(sample: TimeSample) {
        synchronized(sortSamples) {
            sortSamples.add(sample) }
    }

    override fun onRenderEnd(sample: TimeSample) {
        synchronized(renderSamples) {
            renderSamples.add(sample) }
    }
}

internal class TimeSample(
        val start: Long,
        val end: Long,
        val frame: Int
)