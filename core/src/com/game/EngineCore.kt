package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector3
import com.game.util.EntitiesMap
import kotlin.concurrent.thread

internal class EngineCore(
        private val diagnostic: Diagnostic
) {

    private val entitiesList = mutableListOf<Entity>()

    private val lock = java.lang.Object()
    @Volatile private var running = true

    // tmp

    private var cameraPrefs: CameraPrefs = CameraPrefs()

    private var orderBuffer = mutableListOf<Int>()
    private var entitiesBuffer = mutableListOf<Entity>()

    private val finalList = mutableListOf<Pair<Long, Int>>()

    private val tmp = Vector3()

    fun updateEntities(camera: Camera): List<Entity> {
        synchronized(lock) {
            // update camera
            cameraPrefs.update(camera)
            // fill up rendering buffer
            entitiesBuffer.clear()
            orderBuffer.forEach {
                entitiesBuffer.add(entitiesList[it]) }
            // start updating
            lock.notifyAll()
        }
        return entitiesBuffer
    }

    private fun update() {
        diagnostic.beginUpdate()

        // Update
        val delta = Gdx.graphics.deltaTime

        finalList.clear()
        entitiesList.forEachIndexed { index, entity ->
            entity.update(delta)
            val distance = cameraPrefs.position.dst(entity.transform.getTranslation(tmp))
            if (distance < cameraPrefs.far) {
                finalList.add(RenderUtil.getRenderKey(
                        entity.renderable.renderingKey, distance / cameraPrefs.far) to index)
            }
        }

        diagnostic.beginSort()
        orderBuffer.clear()
        SortUtility.keysQsort(finalList)
                .asReversed()
                .forEach { orderBuffer.add(it.second) }
        diagnostic.endSort()

        diagnostic.endUpdate()
    }

    private fun updateLoop() {
        synchronized(lock) {
            lock.wait()
            update()
        }
    }

    fun addEntity(entity: Entity) {
        entitiesList.add(entity)
    }

    fun dispose() {
        running = false
    }

    init {
        thread {
            while (true) {
                updateLoop()
            }
//            var loopTime = 0L
//            var lastTime = System.currentTimeMillis()
//            while (running) {
//                loopTime += System.currentTimeMillis() - lastTime
//                lastTime = System.currentTimeMillis()
//                if (loopTime >= TARGET_FRAME_TIME) {
//                    loopTime -= TARGET_FRAME_TIME.toLong()
//                    update()
//                }
//                val sleepTime = 1000L - loopTime
//                if (sleepTime > 0) sleep(sleepTime)
//            }
        }
    }

    companion object {
        const val TARGET_FRAMERATE = 60
        const val TARGET_FRAME_TIME = 1000f / TARGET_FRAMERATE
    }
}

internal class CameraPrefs {

    var position: Vector3 = Vector3()
    var near: Float = 0f
    var far: Float = 0f

    fun update(camera: Camera) {
        position.set(camera.position)
        near = camera.near
        far = camera.far
    }

}