package com.game.data

import com.game.entity.Entity
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.locks.ReentrantLock



internal class EntitiesBuffer: BufferInterface {

    val addedEntities = ArrayBlockingQueue<Entity>(20_000)

    private val entitiesLock = ReentrantLock()
    val entitiesList = mutableListOf<Entity>()

    override fun addEntity(entity: Entity) {
        addedEntities.put(entity)
    }

    override fun doSafeAction(block: (MutableList<Entity>) -> Unit) {
        entitiesLock.lock()
        try {
            block.invoke(entitiesList)
        } finally {
            entitiesLock.unlock()
        }
    }
}

internal interface BufferInterface {
    fun addEntity(entity: Entity)
    fun doSafeAction(block: (MutableList<Entity>) -> Unit)
}