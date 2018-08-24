package com.game.data

import java.util.concurrent.ArrayBlockingQueue

internal class BlockingBuffer<T> {

    private val lock = java.lang.Object()
    private val unlock = java.lang.Object()
    private val dataQueue = ArrayBlockingQueue<T>(CAPACITY)

    fun insertData(entities: T) {
        dataQueue.put(entities)
        synchronized(lock) { lock.notifyAll() }
        if (dataQueue.size == CAPACITY)
            synchronized(unlock) { unlock.wait() }
    }

    fun processData(block: T.() -> Unit) {
        if (dataQueue.isEmpty())
            synchronized(lock) { lock.wait() }
        if (dataQueue.isNotEmpty()) {
            val data = dataQueue.take()
            synchronized(unlock) { unlock.notifyAll() }
            block.invoke(data)
        }
    }

    companion object {
        const val CAPACITY = 2
    }

}