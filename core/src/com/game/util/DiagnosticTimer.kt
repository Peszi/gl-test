package com.game.util

internal class DiagnosticTimer {

    private var startTime = 0L
    var elapsedTime = 0f

    fun startTimer() {
        elapsedTime = 0f
        startTime = System.nanoTime()
    }

    fun stopTimer() {
        elapsedTime = DiagnosticTimer.getTime(startTime)
    }

    companion object {

        fun getTime(startTime: Long): Float =
                (System.nanoTime() - startTime) / 1000_000f

    }
}