package com.game

import java.time.Instant
import java.time.temporal.ChronoUnit

internal object Log {

    private const val THREAD_LEN = 9

    private const val BLACK = 30
    private const val RED = 31
    private const val GREEN = 32
    private const val YELLOW = 33
    private const val BLUE = 34
    private const val MAGENTA = 35
    private const val CYAN = 36
    private const val WHITE = 37
    private const val DEFAULT = 39

    fun info(message: String) {
        showLog(WHITE, message)
    }

    private fun showLog(color: Int, message: String) {
        println(getColor(MAGENTA) + getCurrentThread() +
                getColor(GREEN) + getCurrentTime() +
                getColor(color) + " $message" + getColor(DEFAULT))
    }

    private fun getColor(color: Int) = 27.toChar() + "[${color}m"

    private fun getCurrentThread(): String {
        var thread = Thread.currentThread().name
        if (thread.length < THREAD_LEN) (0..(THREAD_LEN - thread.length)).forEach { thread += " " }
        return "[${thread.substring(0, Math.min(THREAD_LEN, thread.length))}] "
    }

    private fun getCurrentTime(): String {
        val time = Instant.now().toString().replaceBefore("T", "")
        return time.substring(1, time.length - 1)
    }

}