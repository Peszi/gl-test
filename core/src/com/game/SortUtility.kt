package com.game

internal object SortUtility {

    fun <T:Comparable<T>> qsort(items:List<T>):List<T>{
        if (items.count() < 2) return items
        val pivot = items[items.count()/2]
        val equal = items.filter { it == pivot }
        val less = items.filter { it < pivot }
        val greater = items.filter { it > pivot }
        return qsort(less) + equal + qsort(greater)
    }

    fun keysQsort(keys: List<Pair<Long, Int>>): List<Pair<Long, Int>>{
        if (keys.count() < 2) return keys
        val pivot = keys[keys.count()/2].first
        val equal = keys.filter { it.first == pivot }
        val less = keys.filter { it.first < pivot }
        val greater = keys.filter { it.first > pivot }
        return keysQsort(less) + equal + keysQsort(greater)
    }
}