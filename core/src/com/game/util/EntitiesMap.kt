package com.game.util

import com.game.RenderUtil

internal class EntitiesMap {

    private val entitiesMap = mutableMapOf<Int, MutableList<Int>>()

    fun put(key: Int, entityId: Int) {
        if (entitiesMap.containsKey(key)) {
            entitiesMap[key]!!.add(entityId)
        } else {
            entitiesMap[key] = mutableListOf(entityId)
        }
    }

    fun getSortedEntities(): List<MutableList<Int>> =
            RenderUtil.qsort(entitiesMap.keys.toList()).map { entitiesMap[it] ?: mutableListOf() }.asReversed()

    companion object {

        fun build(entitiesKeys: List<Int>): EntitiesMap {
            val entitiesMap = EntitiesMap()
            entitiesKeys
                    .forEachIndexed { index, i ->  entitiesMap.put(i, index) }
            return entitiesMap
        }
    }

}