package com.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import java.util.*

class GlMain : ApplicationAdapter() {

    private val coreEngine = Engine()

    override fun create() {
        coreEngine.create()

        // Shaders
        val defaultShaderId = coreEngine.resources.loadShader("shaders/simple")

        // Textures
        val boxTextureId = coreEngine.resources.loadTexture("models/orientationBox.png")
        val tombstoneTextureId = coreEngine.resources.loadTexture("models/tombstone.png")

        // Materials
        val transparentMaterialId = coreEngine.resources.addMaterial(MaterialResource(defaultShaderId, boxTextureId, Color(1f, 1f, 1f, .5f)))
        val simpleMaterialId = coreEngine.resources.addMaterial(MaterialResource(defaultShaderId, boxTextureId))
        val tombstoneMaterialId = coreEngine.resources.addMaterial(MaterialResource(defaultShaderId, tombstoneTextureId))

        // Models
        val boxModelId = coreEngine.resources.loadModel("models/orientationBox.obj")
        val tombstoneModelId = coreEngine.resources.loadModel("models/tombstone.obj")

        val random = Random()
        val objectsCount = 30_000
        val translateLimit = objectsCount / 140

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
                0 -> RenderComponent.build(boxModelId, transparentMaterialId)
                1 -> RenderComponent.build(tombstoneModelId, tombstoneMaterialId)
                else -> RenderComponent.build(boxModelId, simpleMaterialId)
            }
            coreEngine.addEntity(Entity(translate, renderable, false))
        }
    }

    override fun resize(width: Int, height: Int) {
        coreEngine.resize(width, height)
    }

    override fun render() {
        coreEngine.render()
    }

    override fun dispose() {
        coreEngine.dispose()
    }
}