package com.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Matrix4
import java.util.*


class GlMain : ApplicationAdapter() {

    private var gameCamera: GameCamera = GameCamera()
    private val mainLoop = MainLoop()

    private lateinit var resources: EngineResources
    private lateinit var renderer: EngineRenderer

    override fun create() {

        resources = EngineResourcesImpl()
        renderer = EngineRenderer(resources)

        val simpleModelId = resources.loadModel("orientationBox.obj")
        val simpleShaderId = resources.loadShader("simple")
        val simpleTextureId = resources.loadTexture("orientationBox.png")
        val simpleMaterialId = resources.addMaterial(MaterialResource(simpleShaderId, simpleTextureId, Color(1f, 1f, 1f, 0.5f)))

        val monkeyModelId = resources.loadModel("tombstone.obj")
        val monkeyTextureId = resources.loadTexture("tombstone.png")
        val monkeyMaterialId = resources.addMaterial(MaterialResource(simpleShaderId, monkeyTextureId))

        val random = Random()
        val objectsCount = 40_000
        val translateLimit = objectsCount / 80
        for (i in 0..objectsCount) {
            val modelChoice: Boolean = random.nextFloat() > 1.95f
            renderer.addEntity(
                    Entity(
                            Matrix4().idt()
                                    .translate(
                                            (random.nextFloat()-.5f) * translateLimit,
                                            (random.nextFloat()-.5f) * translateLimit,
                                            (random.nextFloat()-.5f) * translateLimit)
                                    .rotate(
                                            Vector3(1f, 1f, 1f), random.nextFloat() * 360),
                            RenderComponent(
                                    if (modelChoice) simpleModelId else monkeyModelId,
                                    if (modelChoice) simpleMaterialId else monkeyMaterialId),
                            random
                    )
            )
        }
    }

    fun update() {
//        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) resources.reloadResources()
        gameCamera.update(renderer.camera, Gdx.graphics.deltaTime)
        renderer.update()
    }

    override fun render() {
        update()
        mainLoop.onFrameBegin()

        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDepthMask(true)

        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
        Gdx.gl.glCullFace(GL20.GL_BACK)
        Gdx.gl.glFrontFace(GL20.GL_CCW)

        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D)
        Gdx.gl20.glEnable(GL20.GL_BLEND)
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        renderer.render()

        mainLoop.onFrameEnd()
    }

    override fun dispose() {
        resources.disposeResources()
        mainLoop.dispose()
    }
}