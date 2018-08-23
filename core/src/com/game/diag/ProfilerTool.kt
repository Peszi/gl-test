package com.game.diag

import com.game.TimeSample
import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D

internal class ProfilerTool: JFrame("Profiler") {

    val samplesPanel = SamplesPanel()

    init {
        defaultCloseOperation = JFrame.HIDE_ON_CLOSE
        add(samplesPanel)
        pack()
        setLocationRelativeTo(null)
        isVisible = true
    }

    fun toggleFrame() {
        isVisible = !isVisible
    }

}

internal class SamplesPanel: JPanel()  {

    val BORDER_COLOR = Color(96, 96, 96)
    val BUSY_COLOR = Color(63, 168, 63)
    val WAITING_COLOR = Color(168, 63, 63)

    private var samplesBuffer = listOf(
            mutableListOf<TimeSample>(),
            mutableListOf<TimeSample>(),
            mutableListOf<TimeSample>()
    )

    init {
        preferredSize = Dimension(400, BAR_HEIGHT * samplesBuffer.size + MARGIN * samplesBuffer.size + MARGIN)
        background = Color(32, 32, 32)
    }

    fun updateBuffer(idx: Int, updateSamples: List<TimeSample>) {
        this.samplesBuffer[idx].clear()
        this.samplesBuffer[idx].addAll(updateSamples)
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val g2d = g as Graphics2D


//        if (samplesBuffer.any { it.isEmpty() }) return

        var startTime = Long.MAX_VALUE
        var endTime = 0L
        samplesBuffer
                .filter { it.size > 1 }
                .forEach {
                    startTime = Math.min(startTime, it.first().start)
                    endTime = Math.max(endTime, it.last().end)
                }
        val frameTime = endTime - startTime
        samplesBuffer.forEachIndexed { index, it ->
            val barLength = this.width - BAR_MARGIN * 2 + 1
            val barHeight = BAR_HEIGHT - INNER_MARGIN * 2 + 1
            g2d.color = BORDER_COLOR
            g2d.drawRect(MARGIN, MARGIN, this.width - MARGIN * 2, BAR_HEIGHT)
            g2d.color = WAITING_COLOR
            g2d.fillRect(BAR_MARGIN, BAR_MARGIN, barLength, barHeight)

            var fullBusyTime = 0L

            if (samplesBuffer.size > 1) {
                g2d.color = BUSY_COLOR
                it.forEach {
                    val sampleTime = (it.end - it.start)
                    fullBusyTime += sampleTime
                    val sampleLength = sampleTime / frameTime.toFloat() * barLength
                    val samplePosition = (it.start - startTime) / frameTime.toFloat() * barLength
                    g2d.fillRect(BAR_MARGIN + samplePosition.toInt(), BAR_MARGIN, sampleLength.toInt(), barHeight)
                }
            }

            val avgBusyTime = fullBusyTime / it.size.toFloat()

            g2d.color = Color.WHITE
            g2d.drawString(
                    when(index) {
                        0 -> "UPDATING"
                        1 -> "SORTING"
                        2 -> "RENDERING"
                        else -> "UNDEFINED"
                    } + " avg.${String.format("%.02f", avgBusyTime)}ms updates: ${it.size}"
                    , BAR_HEIGHT / 2, BAR_HEIGHT - 3)
            g2d.translate(0, BAR_HEIGHT + MARGIN)
        }


    }

    fun getMaxHeight() = BAR_HEIGHT * samplesBuffer.size + MARGIN * samplesBuffer.size + MARGIN

    companion object {
        const val MARGIN = 5
        const val INNER_MARGIN = 3
        const val BAR_MARGIN = MARGIN + INNER_MARGIN
        const val BAR_HEIGHT = 26
    }

}