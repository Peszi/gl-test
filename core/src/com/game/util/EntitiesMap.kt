package com.game.util

import com.game.SortUtility

internal class EntitiesMap {

    private val entitiesMap = mutableMapOf<Long, MutableList<Int>>()

    fun put(key: Long, entityId: Int) {
        if (entitiesMap.containsKey(key)) {
            entitiesMap[key]!!.add(entityId)
        } else {
            entitiesMap[key] = mutableListOf(entityId)
        }
    }

    fun getSortedEntities(): List<MutableList<Int>> =
            SortUtility.qsort(entitiesMap.keys.toList())
                    .map { entitiesMap[it] ?: mutableListOf() }

    companion object {

        fun build(entitiesKeys: List<Long>): EntitiesMap {
            val entitiesMap = EntitiesMap()
            entitiesKeys
                    .forEachIndexed { index, i ->  entitiesMap.put(i, index) }
            return entitiesMap
        }
    }

}