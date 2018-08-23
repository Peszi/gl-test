package com.game

import com.badlogic.gdx.Gdx

internal class Engine {

    val resources = EngineResourcesImpl()

    private lateinit var engineCore: EngineBase
    private lateinit var renderer: EngineRenderer

    fun create() {
        engineCore = EngineBase()
        renderer = EngineRenderer(resources, engineCore.diagnostic)
        engineCore.renderer = renderer
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
    }

    fun dispose() {
        resources.disposeResources()
        engineCore.dispose()
    }

    fun addEntity(entity: Entity) {
        engineCore.buffer.addEntity(entity)
    }


}