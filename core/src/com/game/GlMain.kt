package com.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.game.core.EngineCore
import com.game.diag.Log
import com.game.entity.*
import java.util.*

class GlMain : ApplicationAdapter() {

    private lateinit var engineCore: EngineCore

    override fun create() {
        engineCore = EngineCore()
        // Shaders
        val defaultShaderId = engineCore.resources.loadShader("shaders/simple")

        // Textures
        val boxTextureId = engineCore.resources.loadTexture("models/orientationBox.png")
        val tombstoneTextureId = engineCore.resources.loadTexture("models/tombstone.png")

        // Materials
        val transparentMaterialId = engineCore.resources.addMaterial(MaterialResource(defaultShaderId, boxTextureId, Color(1f, 1f, 1f, .5f)))
        val simpleMaterialId = engineCore.resources.addMaterial(MaterialResource(defaultShaderId, boxTextureId))
        val tombstoneMaterialId = engineCore.resources.addMaterial(MaterialResource(defaultShaderId, tombstoneTextureId))

        // Models
        val boxModelId = engineCore.resources.loadModel("models/orientationBox.obj")
        val tombstoneModelId = engineCore.resources.loadModel("models/tombstone.obj")

        val random = Random()
        val objectsCount = 30_000 // 30_000
        val translateLimit = objectsCount / 140

        val renderComponentA = RenderComponent.build(boxModelId, transparentMaterialId)
        val renderComponentB = RenderComponent.build(tombstoneModelId, tombstoneMaterialId)
        val renderComponentC = RenderComponent.build(boxModelId, simpleMaterialId)

        engineCore.buffer.addEntity(CameraController())

        for (i in 0..objectsCount) {
            val modelIdx: Int = random.nextInt(3)
            engineCore.buffer
                    .addEntity(KinematicEntity(random,
                            Vector3((random.nextFloat()-.5f) * translateLimit,
                            (random.nextFloat()-.5f) * translateLimit,
                            (random.nextFloat()-.5f) * translateLimit),
                            when (modelIdx) {
                                0 -> renderComponentA
                                1 -> renderComponentB
                                else -> renderComponentC
                            })
                    )
        }

        Log.info("starting threads ..")
        engineCore.create()
    }

    override fun resize(width: Int, height: Int) {
        engineCore.resize(width.toFloat(), height.toFloat())
    }

    override fun render() {
        engineCore.render()
    }

    override fun dispose() {
        engineCore.dispose()
    }
}