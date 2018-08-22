package com.game

import java.util.concurrent.locks.ReentrantLock



internal class EntitiesBuffer: BufferInterface {

    private val lock = ReentrantLock()
    val entitiesList = mutableListOf<Entity>()

    override fun addEntity(entity: Entity) {
        doSafeAction { entitiesList.add(entity) }
    }

    override fun doSafeAction(block: () -> Unit) {
        lock.lock()
        try {
            block.invoke()
        } finally {
            lock.unlock()
        }
    }
}

internal interface BufferInterface {
    fun addEntity(entity: Entity)
    fun doSafeAction(block: () -> Unit)
}