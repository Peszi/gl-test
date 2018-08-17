package com.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.math.Matrix4
import java.util.*

class GlMain : ApplicationAdapter() {

    private val diagnostic = Diagnostic()

    private val engineLoop = EngineCore(diagnostic)
    private lateinit var resources: EngineResources
    private lateinit var renderer: EngineRenderer

    // tmp

    override fun create() {

        resources = EngineResourcesImpl()
        renderer = EngineRenderer(resources)

        val simpleModelId = resources.loadModel("orientationBox.obj")
        val simpleShaderId = resources.loadShader("simple")
        val simpleTextureId = resources.loadTexture("orientationBox.png")
        val simpleMaterialId = resources.addMaterial(MaterialResource(simpleShaderId, simpleTextureId, Color(1f, 1f, 1f, 1f)))
        val simpleMaterialId2 = resources.addMaterial(MaterialResource(simpleShaderId, simpleTextureId, Color(1f, 1f, 1f, 1f)))

        val monkeyModelId = resources.loadModel("tombstone.obj")
        val monkeyTextureId = resources.loadTexture("tombstone.png")
        val monkeyMaterialId = resources.addMaterial(MaterialResource(simpleShaderId, monkeyTextureId))

        val random = Random()
        val objectsCount = 30_000
        val translateLimit = objectsCount / 80

        for (i in 0..objectsCount) {
            val modelIdx: Int = random.nextInt(3)
            val translate = Matrix4()
                    .translate(
                            (random.nextFloat()-.5f) * translateLimit,
                            (random.nextFloat()-.5f) * translateLimit,
                            (random.nextFloat()-.5f) * translateLimit)
                    .rotate(
                            Vector3(random.nextFloat(), random.nextFloat(), random.nextFloat()), random.nextFloat() * 360)
            val renderable = when (modelIdx) {
                0 -> RenderComponent.build(simpleModelId, simpleMaterialId)
                1 -> RenderComponent.build(monkeyModelId, monkeyMaterialId)
                else -> RenderComponent.build(simpleModelId, simpleMaterialId2)
            }
            engineLoop.addEntity(Entity(translate, renderable, false))
        }
    }

    override fun render() {
        diagnostic.beginFrame()
        val renderBuffer = engineLoop.updateEntities(renderer.camera)
        renderer.render(renderBuffer.orderBuffer, renderBuffer.entitiesBuffer)
        diagnostic.endFrame()
    }

    override fun dispose() {
        resources.disposeResources()
        engineLoop.dispose()
    }
}