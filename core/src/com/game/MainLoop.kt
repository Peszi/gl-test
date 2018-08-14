package com.game

import java.lang.Thread.sleep
import kotlin.concurrent.thread

class MainLoop {

    @Volatile private var framesCount = 0
    @Volatile private var framesTime = 0

    private var frameBeginTime = System.currentTimeMillis()

    init {
        thread {
            var frameTime = 0L
            var lastTime = System.currentTimeMillis()
            while (true) {
                frameTime += System.currentTimeMillis() - lastTime
                lastTime = System.currentTimeMillis()
                if (frameTime >= 1000) {
                    frameTime -= 1000
                    val diagMessage = "fps $framesCount render time $framesTime ms"
                    if (framesTime <= TARGET_FRAME_TIME) println(diagMessage) else System.err.println(diagMessage)
                    framesCount = 0
                }
                sleep(1000L - frameTime)
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


    companion object {

        private const val TARGET_FRAMERATE = 60
        private const val TARGET_FRAME_TIME = 1000f / TARGET_FRAMERATE

    }

}