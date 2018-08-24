package com.game.diag

internal object DiagTimer {

    fun getTimeStamp() = System.currentTimeMillis()

    fun getTime(startTime: Long): Float =
            (System.nanoTime() - startTime) / 1000_000f

}