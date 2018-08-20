package com.game

internal class CoreEngine {

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
        // update
        val entities = engineLoop.waitForEntities(renderer.gameCamera.camera)
        engineLoop.diagnosticTimer.elapsedTime
        engineLoop.requestUpdate()

        renderer.render(entities)
        renderer.diagnosticTimer.elapsedTime
    }

    fun dispose() {
        resources.disposeResources()
        engineLoop.dispose()
    }

    fun addEntity(entity: Entity) {
        engineLoop.addEntity(entity)
    }


}