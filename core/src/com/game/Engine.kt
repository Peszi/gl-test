package com.game

import kotlinx.coroutines.experimental.async

internal class Engine {

    val resources = EngineResourcesImpl()

    private val diagnostic = DiagnosticImpl()
    private val engineLoop = EngineCore(diagnostic)
    private lateinit var renderer: EngineRenderer

    fun create() {
        renderer = EngineRenderer(resources)
    }

    fun resize(width: Int, height: Int) {
        renderer.resize(width.toFloat(), height.toFloat())
    }

    fun render() {
        // update logic 1
        // render logic 1
        // request frame


        // update
        val entities = engineLoop.waitForEntities(renderer.gameCamera.camera)
        engineLoop.diagnosticTimer.elapsedTime

        renderer.render(entities)
        println("frame ${renderer.diagnosticTimer.elapsedTime}")
    }

    fun dispose() {
        resources.disposeResources()
        engineLoop.dispose()
    }

    fun addEntity(entity: Entity) {
        engineLoop.addEntity(entity)
    }


}