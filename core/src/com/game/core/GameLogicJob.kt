package com.game.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.game.data.RenderBuffer
import com.game.data.RenderData
import com.game.diag.DiagTimer
import com.game.diag.TimeSample
import com.game.entity.RenderableEntity
import com.main.threading.JobDesc

internal class GameLogicJob(
        engineCore: EngineCore
): JobDesc(
        {
            while (true) {
                val startTime = DiagTimer.getTimeStamp()
                addAwaitingEntities(engineCore)
                handleGameInputs(engineCore)
                updateGameLogic(engineCore, Gdx.graphics.deltaTime)
                val renderBuffer = getRenderableBuffer(engineCore)
                engineCore.diagnostic.onUpdateEnd(TimeSample(startTime, DiagTimer.getTimeStamp(), renderBuffer.gameState.frameIdx))

                engineCore.buffer.doSafeAction {
                    engineCore.updateQueue.insertData(renderBuffer) }
            }
        }
) {
    companion object {

        fun addAwaitingEntities(engineCore: EngineCore) {
            while (true) {
                if (engineCore.buffer.addedEntities.isEmpty()) break
                val entity = engineCore.buffer.addedEntities.take()
                engineCore.buffer.doSafeAction { it.add(entity) }
                entity.start(engineCore)
            }
        }

        fun handleGameInputs(engineCore: EngineCore) {
            // Engine Inputs
            if (Gdx.input.isKeyJustPressed(Input.Keys.P))
                engineCore.profiler.toggleFrame()
        }

        fun updateGameLogic(engineCore: EngineCore, deltaTime: Float) {
            // Updates
            val deltaTime = Gdx.graphics.deltaTime
            engineCore.buffer.doSafeAction {
                engineCore.buffer.entitiesList.forEach { it.update(deltaTime, engineCore) }
            }
            engineCore.camera.camera.update()
            engineCore.state.update(deltaTime, engineCore)
        }

        fun getRenderableBuffer(engineCore: EngineCore): RenderBuffer {

            val renderableList = (engineCore.buffer.entitiesList
                    .filter { it is RenderableEntity } as List<RenderableEntity>)
                    .filter { it.renderable != null }
                    .map { RenderData(it.transform, it.renderable!!) }

            engineCore.frameIdx++
            if (engineCore.frameIdx >= 6)
                engineCore.frameIdx = 0

            return RenderBuffer(
                    engineCore.state, renderableList
            )
        }
    }
}