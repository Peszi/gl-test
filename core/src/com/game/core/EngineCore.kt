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
import com.game.entity.Entity
import com.game.render.MainCamera
import com.main.threading.ThreadsImpl
import com.main.threading.ThreadsInterface
import java.util.concurrent.ArrayBlockingQueue

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

    val camera: MainCamera = MainCamera()

    // Outer
    val resources = EngineResourcesImpl()
    val buffer = EntitiesBuffer()

    // TMP

    var frameIdx = 0

    fun create() {
        coreThreads.runJobs(listOf(GameInitJob(this)))
        renderer = EngineRenderer(resources, renderQueue, diagnostic)
    }

    fun resize(width: Float, height: Float) {
        camera.updateViewport(width, height)
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
        position = engineCore.camera.camera.position
        combined = engineCore.camera.camera.combined
    }

    // TMP

    var frameIdx = 0
}