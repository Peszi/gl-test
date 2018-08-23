package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.game.diag.ProfilerTool
import com.game.util.DiagnosticTimer
import com.main.threading.AtomicCounter
import com.main.threading.JobDesc
import com.main.threading.ThreadsImpl
import com.main.threading.ThreadsInterface

internal class EngineBase {

    val profiler = ProfilerTool()
    val diagnostic = DiagnosticImpl(profiler)

    val buffer = EntitiesBuffer()
    val threads: ThreadsInterface = ThreadsImpl()

    lateinit var renderer: RenderInterface

    fun startLoop() {
        threads.runJobs(listOf(GameLoopJob(this)))
    }

    fun dispose() {}
}

internal class GameLoopJob(
        engineBase: EngineBase
): JobDesc (
        {
            val gameLogicJob = GameLogicJob(engineBase)
            val renderLogicJob = RenderLogicJob(engineBase)

            while (true) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.P))
                    engineBase.profiler.toggleFrame()
                val counter = AtomicCounter(2)
                engineBase.threads.runJobs(listOf(
                        gameLogicJob, renderLogicJob
                ), counter)
                engineBase.threads.waitForDone(counter)

                engineBase.buffer.doSafeAction {
                    engineBase.renderer.requestFrame(
                            engineBase.buffer.entitiesList
                    )
                }
            }
        }
)

internal class GameLogicJob(
        engineBase: EngineBase
): JobDesc(
        {
            val startTime = DiagnosticTimer.getTimeStamp()
            val deltaTime = Gdx.graphics.deltaTime
            engineBase.buffer.doSafeAction {
                engineBase.buffer.entitiesList.forEach { it.update(deltaTime) }
            }
            Thread.sleep(10)
            engineBase.diagnostic.onGameLogicEnd(startTime, DiagnosticTimer.getTimeStamp())
        }
)

internal class RenderLogicJob(
        engineBase: EngineBase
): JobDesc(
        {
            val tmp = Vector3()
            engineBase.buffer.doSafeAction {
                engineBase.buffer.entitiesList.forEachIndexed { index, entity ->
                    val distance = cameraPrefs.position.dst(entity.transform.getTranslation(tmp))
                    if (distance < cameraPrefs.far) {
                        finalList.add(RenderUtil.getRenderKey(
                                entity.renderable.renderingKey, distance / cameraPrefs.far) to index)
                    }
                }
            }
            entitiesList.forEachIndexed { index, entity ->

            }
            orderBuffer.clear()
            SortUtility.keysQsort(finalList)
                    .asReversed()
                    .forEach { orderBuffer.add(it.second) }
        }
)