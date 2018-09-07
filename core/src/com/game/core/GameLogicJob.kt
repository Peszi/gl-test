package com.game.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.game.data.RenderBuffer
import com.game.data.RenderData
import com.game.diag.DiagTimer
import com.game.diag.TimeSample
import com.main.threading.JobDesc

internal class GameLogicJob(
        engineCore: EngineCore
): JobDesc(
        {
            while (true) {

                addAwaitingEntities(engineCore)
                handleGameInputs(engineCore)
                updateGameLogic(engineCore, Gdx.graphics.deltaTime)
                frustumCulling(engineCore)

                val startTime = DiagTimer.getTimeStamp()
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
            engineCore.mainCamera.update()
            // Updates
            engineCore.buffer.doSafeAction {
                engineCore.buffer.entitiesList.forEach { it.update(deltaTime, engineCore) }
            }
            engineCore.state.update(deltaTime, engineCore)
        }

        fun frustumCulling(engineCore: EngineCore) {

        }

        fun getRenderableBuffer(engineCore: EngineCore): RenderBuffer {

            val tmp = Vector3()
            val tmp2 = Vector3()

            val renderableList = engineCore.buffer.entitiesList
                    .filter { it.renderable != null }
                    .filter {
                        val boundingBox = engineCore.resources.getBoundingBox(it.renderable!!.meshId)
                        it.transform.getTranslation(tmp)
                        boundingBox.getDimensions(tmp2)
                        engineCore.mainCamera.camera.frustum.boundsInFrustum(tmp, tmp2)
                    }

            return RenderBuffer(
                    engineCore.state, renderableList.map { RenderData(it.transform, it.renderable!!) }
            )
        }
    }
}