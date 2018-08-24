package com.game.render

internal object SortUtility {

    fun <T:Comparable<T>> qsort(items:List<T>):List<T>{
        if (items.count() < 2) return items
        val pivot = items[items.count()/2]
        val equal = items.filter { it == pivot }
        val less = items.filter { it < pivot }
        val greater = items.filter { it > pivot }
        return qsort(less) + equal + qsort(greater)
    }
}