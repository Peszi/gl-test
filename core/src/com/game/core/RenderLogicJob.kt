package com.game.core

import com.badlogic.gdx.math.Vector3
import com.game.data.RenderBuffer
import com.game.data.RenderData
import com.game.render.MainCamera
import com.game.render.RenderUtil
import com.game.diag.DiagTimer
import com.game.diag.TimeSample
import com.main.threading.JobDesc

internal class RenderLogicJob(
        engineCore: EngineCore
): JobDesc(
        {
            while (true) {
                val tmp = Vector3()
                val startTime = DiagTimer.getTimeStamp()
                val keysBuffer: MutableList<Pair<Long, RenderData>> = mutableListOf()

                var gameState: GameState? = null
                engineCore.updateQueue.processData {
                    gameState = this.gameState
                    this.renderableList.forEach {
                        val distance = this.gameState.position.dst(it.transform.getTranslation(tmp)) // TODO tmp
                        if (distance < MainCamera.CAMERA_FAR) {
                            keysBuffer.add(RenderUtil.getRenderKey(
                                    it.renderable.renderingKey, distance / MainCamera.CAMERA_FAR) to it)
                        }
                    }
                }

                val renderList = keysQsort(keysBuffer)
                        .map { it.second }.asReversed()
                engineCore.diagnostic.onSortEnd(TimeSample(startTime, DiagTimer.getTimeStamp(), gameState?.frameIdx ?: 0))

                gameState?.let {
                    engineCore.renderQueue.insertData(RenderBuffer(it, renderList)) }
            }
        }
) {
    companion object {

        fun <T> keysQsort(keys: List<Pair<Long, T>>): List<Pair<Long, T>>{
            if (keys.count() < 2) return keys
            val pivot = keys[keys.count()/2].first
            val equal = keys.filter { it.first == pivot }
            val less = keys.filter { it.first < pivot }
            val greater = keys.filter { it.first > pivot }
            return keysQsort(less) + equal + keysQsort(greater)
        }

    }
}