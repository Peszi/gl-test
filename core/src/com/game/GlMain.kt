package com.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.glutils.VertexBufferObject




class GlMain : ApplicationAdapter() {

    private var gameCamera: GameCamera = GameCamera()
    private val mainLoop = MainLoop()

    private lateinit var engineResources: EngineResources
    private lateinit var engineRenderer: EngineRenderer

    override fun create() {
        engineResources = EngineResourcesImpl()
        engineRenderer = EngineRenderer()

        engineResources.loadShader("simple")
        engineResources.loadModel("orientationBox.obj")
        engineResources.loadTexture("orientationBox.png")

        engineRenderer.entitiesList.add(
                Entity(
                        Matrix4().idt(),
                        RenderComponent(
                                "simple",
                                "orientationBox.png",
                                "orientationBox.obj"
                        )
                )
        )
    }

    fun update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5))
            engineResources.reloadResources()
        gameCamera.update(engineRenderer.camera, Gdx.graphics.deltaTime)
    }

    override fun render() {
        update()
        mainLoop.onFrameBegin()

        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDepthMask(false)
        Gdx.gl.glDisable(GL20.GL_BLEND)
        Gdx.gl.glEnable(GL20.GL_CULL_FACE)

        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D)
        Gdx.gl20.glEnable(GL20.GL_BLEND)
//        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        engineRenderer.render(engineResources)

        mainLoop.onFrameEnd()
    }

    override fun dispose() {
        engineResources.disposeResources()
    }
}