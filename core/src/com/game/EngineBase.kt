package com.game

import com.badlogic.gdx.Gdx
import com.main.threading.AtomicCounter
import com.main.threading.JobDesc
import com.main.threading.ThreadsImpl
import com.main.threading.ThreadsInterface
import kotlin.concurrent.thread

internal class EngineBase(
        val renderer: RenderInterface
) {

    val buffer = EntitiesBuffer()
//    val threads: ThreadsInterface = ThreadsImpl()

    init {

    }

    fun startLoop() {
        thread(name = "WORKER") {
            while (true) {
//                val counter = AtomicCounter(1)
//                threads.runJobs(listOf(GameLogicJob(buffer)), counter)
//                threads.waitForDone(counter)

//
//                renderer.requestFrame(buffer.entitiesList)
//                buffer.doSafeAction {
//
//                }
                renderer.requestFrame(buffer.entitiesList)
            }
        }
        Log.info("Loop STARTED!!!")
    }

    fun dispose() {

    }
}

internal class MainJob(
        engineBase: EngineBase
): JobDesc (
        {
            val counter = AtomicCounter(1)
//            engineBase.threads.runJobs(listOf(GameLogicJob(engineBase.buffer)), counter)
//            engineBase.threads.waitForDone(counter)

            engineBase.buffer.doSafeAction {
                engineBase.renderer.requestFrame(
                        engineBase.buffer.entitiesList
                )
            }
        }
)

internal class GameLogicJob(
        buffer: EntitiesBuffer
): JobDesc(
        {
            val deltaTime = Gdx.graphics.deltaTime
            buffer.doSafeAction {
                buffer.entitiesList.forEach { it.update(deltaTime) }
            }
        }
)

internal class RenderLogicJob: JobDesc(
        {}
)