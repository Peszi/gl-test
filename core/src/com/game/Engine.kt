package com.game

import com.badlogic.gdx.Gdx
import kotlin.concurrent.thread

internal class Engine {

    val resources = EngineResourcesImpl()
    private val diagnostic = DiagnosticImpl()

    private lateinit var engineCore: EngineBase
    private lateinit var renderer: EngineRenderer

    fun create() {
        renderer = EngineRenderer(resources)
        engineCore = EngineBase(renderer)
    }

    fun start() {
        Gdx.graphics.requestRendering()
        engineCore.startLoop()
    }

    fun resize(width: Int, height: Int) {
        renderer.resize(width.toFloat(), height.toFloat())
    }

    fun render() {
        renderer.render()
//        println("frame ${renderer.diagnosticTimer.elapsedTime}")
    }

    fun dispose() {
        resources.disposeResources()
        engineCore.dispose()
    }

    fun addEntity(entity: Entity) {
        engineCore.buffer.addEntity(entity)
    }


}