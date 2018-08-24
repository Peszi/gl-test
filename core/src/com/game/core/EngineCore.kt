package com.game.core

import com.badlogic.gdx.math.Vector3
import com.game.diag.DiagnosticImpl
import com.game.engine.EngineRenderer
import com.game.engine.EngineResourcesImpl
import com.game.data.BlockingBuffer
import com.game.data.EntitiesBuffer
import com.game.data.RenderBuffer
import com.game.diag.ProfilerTool
import com.main.threading.ThreadsImpl
import com.main.threading.ThreadsInterface

internal class EngineCore {

    // Tools
    val profiler = ProfilerTool()
    val diagnostic = DiagnosticImpl(profiler)

    // Inner
    val coreThreads: ThreadsInterface = ThreadsImpl()
    lateinit var renderer: EngineRenderer

    val updateQueue = BlockingBuffer<RenderBuffer>()
    val renderQueue = BlockingBuffer<RenderBuffer>()

    var elapsedTime = 0f

    // Outer
    val resources = EngineResourcesImpl()
    val buffer = EntitiesBuffer()

    // TMP

    var position: Vector3 = Vector3()
    var direction: Vector3 = Vector3(0f, 0f, -1f)

    fun create() {
        coreThreads.runJobs(listOf(GameInitJob(this)))
        renderer = EngineRenderer(resources, renderQueue, diagnostic)
    }

    fun resize(width: Float, height: Float) {
        renderer.resize(width, height)
    }

    fun render() {
        renderer.render()
    }

    fun dispose() {
        resources.disposeResources()
    }
}