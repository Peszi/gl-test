package com.game.diag

import java.awt.Dimension
import javax.swing.JFrame
import javax.swing.JPanel
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import kotlin.math.sign

internal class ProfilerTool: JFrame("Profiler") {

    val samplesPanel = SamplesPanel(this)

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

internal class SamplesPanel(val frame: JFrame): KeyListener, JPanel()  {

    private val BORDER_COLOR = Color(96, 96, 96)
    private val BUSY_COLOR = Color(63, 168, 63)
    private val WAITING_COLOR = Color(168, 63, 63)
    private val BACKGROUND_COLOR = Color(32, 32, 32)

    var running = true
    var vertical = false
    var zoom = 1f
    var position = 0f

    private var samplesBuffer = listOf(
            mutableListOf<TimeSample>(),
            mutableListOf<TimeSample>(),
            mutableListOf<TimeSample>()
    )

    init {
        preferredSize = Dimension(400, BAR_HEIGHT * samplesBuffer.size + MARGIN * samplesBuffer.size + MARGIN)
        background = BACKGROUND_COLOR
        isFocusable = true
        addKeyListener(this)
        addMouseWheelListener {
            if (vertical) {
                zoom -= it.wheelRotation.sign
                zoom = Math.max(1f, zoom)
                zoom = Math.min(10f, zoom)
            } else {
                position -= it.wheelRotation.sign * -0.01f
                position = Math.max(0f, position)
                position = Math.min(1f, position)
            }
            setupTitle()
            this.repaint()
        }
    }

    fun updateBuffer(idx: Int, updateSamples: List<TimeSample>) {
        if (running) {
            synchronized(samplesBuffer) {
                samplesBuffer[idx].clear()
                samplesBuffer[idx].addAll(updateSamples)
            }
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val g2d = g as Graphics2D

        var startTime = Long.MAX_VALUE
        var endTime = 0L
        synchronized(samplesBuffer) {
            samplesBuffer
                    .filter { it.size > 1 }
                    .forEach {
                        startTime = Math.min(startTime, it.first().start)
                        endTime = Math.max(endTime, it.last().end)
                    }
            val frameTime = ((endTime - startTime) / zoom).toLong()
            startTime += ((endTime - frameTime - startTime) * position).toLong()
            samplesBuffer.forEachIndexed { index, it ->
                val barLength = this.width - BAR_MARGIN * 2 + 1
                val barHeight = BAR_HEIGHT - INNER_MARGIN * 2 + 1
                g2d.color = BORDER_COLOR
                g2d.drawRect(MARGIN, MARGIN, this.width - MARGIN * 2, BAR_HEIGHT)
                g2d.color = WAITING_COLOR
                g2d.fillRect(BAR_MARGIN, BAR_MARGIN, barLength, barHeight)

                var fullBusyTime = 0L

                if (samplesBuffer.size > 1) {
                    it.forEach {
                        val sampleTime = (it.end - it.start)
                        fullBusyTime += sampleTime
                        val sampleLength = sampleTime / frameTime.toFloat() * barLength
                        val samplePosition = (it.start - startTime) / frameTime.toFloat() * barLength
                        g2d.color = BUSY_COLOR
                        g2d.fillRect(BAR_MARGIN + samplePosition.toInt(), BAR_MARGIN, sampleLength.toInt(), barHeight)
                        g2d.color = BACKGROUND_COLOR
                        g2d.drawLine(BAR_MARGIN + samplePosition.toInt(), BAR_MARGIN,
                                BAR_MARGIN + samplePosition.toInt(), BAR_MARGIN + barHeight - 1)
                    }
                }

                val avgBusyTime = fullBusyTime / it.size.toFloat()

                g2d.color = Color.WHITE
                g2d.drawString(
                        when (index) {
                            0 -> "UPDATING"
                            1 -> "SORTING"
                            2 -> "RENDERING"
                            else -> "UNDEFINED"
                        } + " avg.${String.format("%.02f", avgBusyTime)}ms updates: ${it.size}"
                        , BAR_HEIGHT / 2, BAR_HEIGHT - 3)
                g2d.translate(0, BAR_HEIGHT + MARGIN)
            }
        }
    }

    fun setupTitle() {
        frame.title = "Profiler (zoom: ${zoom}x offset: ${(position * 100).toInt()}%) " + if (!running) "PAUSED" else ""
    }

    override fun keyTyped(e: KeyEvent?) {}

    override fun keyPressed(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_CONTROL) vertical = true
    }

    override fun keyReleased(e: KeyEvent?) {
        if (e?.keyCode == KeyEvent.VK_CONTROL) vertical = false
        if (e?.keyCode == KeyEvent.VK_SPACE) running = !running
        setupTitle()
    }

    fun getMaxHeight() = BAR_HEIGHT * samplesBuffer.size + MARGIN * samplesBuffer.size + MARGIN

    companion object {
        const val MARGIN = 5
        const val INNER_MARGIN = 3
        const val BAR_MARGIN = MARGIN + INNER_MARGIN
        const val BAR_HEIGHT = 26
    }

}