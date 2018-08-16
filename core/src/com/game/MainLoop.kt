package com.game

import java.lang.Thread.sleep
import kotlin.concurrent.thread

class MainLoop {

    @Volatile private var running = true
    @Volatile private var framesCount = 0
    @Volatile private var framesTime = 0

    private var frameBeginTime = System.currentTimeMillis()

    init {
        thread {
            var frameTime = 0L
            var lastTime = System.currentTimeMillis()
            while (running) {
                frameTime += System.currentTimeMillis() - lastTime
                lastTime = System.currentTimeMillis()
                if (frameTime >= 1000) {
                    frameTime -= 1000
                    val diagMessage = "fps $framesCount renderable time ${framesTime}ms of ${TARGET_FRAME_TIME}ms"
                    if (framesTime <= TARGET_FRAME_TIME) println(diagMessage) else System.err.println(diagMessage)
                    framesCount = 0
                }
                val sleepTime = 1000L - frameTime
                if (sleepTime > 0) sleep(sleepTime)
            }
        }
    }

    fun onFrameBegin() {
        frameBeginTime = System.currentTimeMillis()
    }

    fun onFrameEnd() {
        framesTime = (System.currentTimeMillis() - frameBeginTime).toInt()
        framesCount++
    }

    fun dispose() {
        running = false
    }

    companion object {

        private const val TARGET_FRAMERATE = 60
        private const val TARGET_FRAME_TIME = 1000f / TARGET_FRAMERATE

    }

}