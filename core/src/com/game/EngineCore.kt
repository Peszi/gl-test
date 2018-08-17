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
    private val entitiesKeys = mutableListOf<Long>()

    private val lock = java.lang.Object()
    @Volatile private var running = true

    // tmp

    private var cameraPrefs: CameraPrefs = CameraPrefs()

    private var orderBuffer = listOf<MutableList<Int>>()
    private var entitiesBuffer = mutableListOf<Entity>()

    fun updateEntities(camera: Camera): RenderBuffer {
//        cameraPrefs.update(camera)
        entitiesBuffer.clear()
        entitiesList.forEach{ entitiesBuffer.add(it) }
        synchronized(lock) { lock.notifyAll() }
        return RenderBuffer(orderBuffer, entitiesBuffer)
    }

    private fun update() {
        diagnostic.beginUpdate()

        // Update
        val delta = Gdx.graphics.deltaTime
        entitiesList.forEach { it.update(delta) }

//        entitiesKeys.forEachIndexed { index, _ ->
//            RenderUtil.updateRenderKey(entitiesList[index], cameraPrefs) }

        orderBuffer = EntitiesMap.build(
                entitiesKeys.indices.map {
                    RenderUtil.updateRenderKey(entitiesList[it], cameraPrefs)
                }
        ).getSortedEntities()

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
        entitiesKeys.add(entity.renderable.renderingKey)
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

internal class RenderBuffer(
        val orderBuffer: List<MutableList<Int>>,
        val entitiesBuffer: MutableList<Entity>
)

internal class CameraPrefs {

    var position: Vector3 = Vector3.Zero
    var near: Float = 0f
    var far: Float = 0f

    fun update(camera: Camera) {
        position.set(camera.position)
        near = camera.near
        far = camera.far
    }

}