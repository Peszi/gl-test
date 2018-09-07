package com.game.core

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.game.diag.DiagnosticImpl
import com.game.engine.EngineRenderer
import com.game.engine.EngineResourcesImpl
import com.game.data.BlockingBuffer
import com.game.data.EntitiesBuffer
import com.game.data.RenderBuffer
import com.game.diag.ProfilerTool
import com.game.render.MainCamera
import com.main.threading.ThreadsImpl
import com.main.threading.ThreadsInterface

internal class EngineCore {

    // Tools
    val profiler = ProfilerTool()
    val diagnostic = DiagnosticImpl(profiler)

    // Inner
    val coreThreads: ThreadsInterface = ThreadsImpl()
    lateinit var renderer: EngineRenderer

    val state = GameState()
    val updateQueue = BlockingBuffer<RenderBuffer>()
    val renderQueue = BlockingBuffer<RenderBuffer>()

    val mainCamera: MainCamera = MainCamera()

    // Outer
    val resources = EngineResourcesImpl()
    val buffer = EntitiesBuffer()

    // TMP

    var frameIdx = 0

    fun create() {
        val vertices = mutableListOf<Float>()
        val planePoints = mainCamera.camera.frustum.planePoints

        // Near
        listOf(
                planePoints[0], planePoints[1], planePoints[1], planePoints[2],
                planePoints[2], planePoints[3], planePoints[3], planePoints[0]
        ).forEach { vertices.addAll(listOf(it.x, it.y, it.z)) }

        // Far
        listOf(
                planePoints[4], planePoints[5], planePoints[5], planePoints[6],
                planePoints[6], planePoints[7], planePoints[7], planePoints[4]
        ).forEach { vertices.addAll(listOf(it.x, it.y, it.z)) }

        // Sides
        listOf(
                planePoints[0], planePoints[4], planePoints[1], planePoints[5],
                planePoints[2], planePoints[6], planePoints[3], planePoints[7]
        ).forEach { vertices.addAll(listOf(it.x, it.y, it.z)) }

        resources.getModel(0).setVertices(vertices.toFloatArray())

        coreThreads.runJobs(listOf(GameInitJob(this)))
        renderer = EngineRenderer(resources, renderQueue, diagnostic)
    }

    fun resize(width: Float, height: Float) {
        mainCamera.updateViewport(width, height)
    }

    fun render() {
        renderer.render()
    }

    fun dispose() {
        resources.disposeResources()
    }
}

internal class GameState {

    var position: Vector3 = Vector3()
    var combined: Matrix4 = Matrix4()

    var elapsedTime = 0f

    fun update(delta: Float, engineCore: EngineCore) {
        elapsedTime += delta
        position = engineCore.mainCamera.camera.position
        combined = engineCore.mainCamera.camera.combined
    }

    // TMP

    var frameIdx = 0
}