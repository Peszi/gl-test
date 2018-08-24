package com.game.core

import com.badlogic.gdx.math.Vector3
import com.game.data.RenderBuffer
import com.game.data.RenderData
import com.game.data.RenderPrefs
import com.game.render.GameCamera
import com.game.render.RenderUtil
import com.game.diag.DiagTimer
import com.main.threading.JobDesc

internal class RenderLogicJob(
        engineCore: EngineCore
): JobDesc(
        {
            while (true) {
                val tmp = Vector3()
                val startTime = DiagTimer.getTimeStamp()
                val keysBuffer: MutableList<Pair<Long, RenderData>> = mutableListOf()

                var renderPrefs: RenderPrefs? = null
                engineCore.updateQueue.processData {
                    renderPrefs = this.renderPrefs
                    this.renderableList.forEach {
                        val distance = Vector3(0f, 0f, 0f).dst(it.transform.getTranslation(tmp)) // TODO tmp
                        if (distance < GameCamera.CAMERA_FAR) {
                            keysBuffer.add(RenderUtil.getRenderKey(
                                    it.renderable.renderingKey, distance / GameCamera.CAMERA_FAR) to it)
                        }
                    }
                }

                val renderList = keysQsort(keysBuffer).map { it.second }.asReversed()
                engineCore.diagnostic.onSortEnd(startTime, DiagTimer.getTimeStamp())

                renderPrefs?.let {
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