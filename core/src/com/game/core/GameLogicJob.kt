package com.game.core

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.game.data.RenderBuffer
import com.game.data.RenderData
import com.game.data.RenderPrefs
import com.game.diag.DiagTimer
import com.game.entity.CameraController
import com.game.entity.RenderableEntity
import com.main.threading.JobDesc
import javafx.scene.Camera

internal class GameLogicJob(
        engineCore: EngineCore
): JobDesc(
        {
            while (true) {
                val startTime = DiagTimer.getTimeStamp()
                handleGameInputs(engineCore)
                updateGameLogic(engineCore, Gdx.graphics.deltaTime)
                val renderBuffer = getRenderableBuffer(engineCore)
                engineCore.diagnostic.onUpdateEnd(startTime, DiagTimer.getTimeStamp())

                engineCore.buffer.doSafeAction {
                    engineCore.updateQueue.insertData(renderBuffer) }
            }
        }
) {
    companion object {

        fun handleGameInputs(engineCore: EngineCore) {
            // Engine Inputs
            if (Gdx.input.isKeyJustPressed(Input.Keys.P))
                engineCore.profiler.toggleFrame()
        }

        fun updateGameLogic(engineCore: EngineCore, deltaTime: Float) {
            // Updates
            engineCore.elapsedTime += Gdx.graphics.deltaTime
            engineCore.buffer.doSafeAction {
                engineCore.buffer.entitiesList.forEach {
                    it.input(0.0166f, engineCore)
                    it.update(0.0166f) }
            }
        }

        fun getRenderableBuffer(engineCore: EngineCore): RenderBuffer {

            val renderableList = (engineCore.buffer.entitiesList
                    .filter { it is RenderableEntity } as List<RenderableEntity>)
                    .map { RenderData(it.transform, it.renderable) }

            return RenderBuffer(
                    RenderPrefs(
                            engineCore.position, engineCore.direction, 0f
                    ), renderableList
            )
        }
    }
}