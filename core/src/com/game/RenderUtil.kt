package com.game

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3

internal class RenderUtil {

    companion object {

        fun generateKey(entity: Entity, camera: Camera): Int {
            var entityKey: Int = camera.position.dst(entity.transform.getTranslation(Vector3())).toInt()
            return entityKey
        }

        fun <T:Comparable<T>> qsort(items:List<T>):List<T>{
            if (items.count() < 2) return items
            val pivot = items[items.count()/2]
            val equal = items.filter { it == pivot }
            val less = items.filter { it < pivot }
            val greater = items.filter { it > pivot }
            return qsort(less) + equal + qsort(greater)
        }
    }
}