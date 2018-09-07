package com.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
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

        // Line render
        val modelBuilder = ModelBuilder()

        // Cone
        modelBuilder.begin()
        val coneBuilder = modelBuilder.part("cone", GL20.GL_LINES, VertexAttributes(VertexAttribute.Position()), Material())
        coneBuilder.setColor(Color.RED)
        (1..12).forEach {
            coneBuilder.line(0.0f, it.toFloat(), 0.0f, 0.0f, it.toFloat(), 0.0f)
        }

//        coneBuilder.line(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
//        coneBuilder.line(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
//        coneBuilder.line(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
        val coneMesh = modelBuilder.end().meshes.first()

//        coneMesh.setVertices(
//                (0..71).map { 0f }.toFloatArray()
//        )

        // Grid
//        modelBuilder.begin()
//        val builder = modelBuilder.part("line", 1, 3, Material())
//        builder.setColor(Color.RED)
//        for (x in 0..10) {
//            val offset = 5f - x * 1f
//            builder.line(offset, 0.0f, -5f, offset, 0.0f, 5f)
//            builder.line(-5f, 0.0f, offset, 5f, 0.0f, offset)
//        }
//        val gridMesh = modelBuilder.end().meshes.first()

        // Shaders
        val defaultShaderId = engineCore.resources.loadShader("shaders/default")
        val simpleShaderId = engineCore.resources.loadShader("shaders/simple")

        // Textures
        val boxTextureId = engineCore.resources.loadTexture("models/orientationBox.png")
        val tombstoneTextureId = engineCore.resources.loadTexture("models/tombstone.png")

        // Materials
        val lineMaterialId = engineCore.resources.addMaterial(MaterialResource(defaultShaderId, 0, Color(0.8f, 0.8f, 0.8f, 0.8f), GL20.GL_LINES))
        val transparentMaterialId = engineCore.resources.addMaterial(MaterialResource(simpleShaderId, boxTextureId, Color(1f, 1f, 1f, .5f)))
        val simpleMaterialId = engineCore.resources.addMaterial(MaterialResource(simpleShaderId, boxTextureId))
        val tombstoneMaterialId = engineCore.resources.addMaterial(MaterialResource(simpleShaderId, tombstoneTextureId))
        val centerMaterialId = engineCore.resources.addMaterial(MaterialResource(simpleShaderId, boxTextureId, Color(1f, 0f, 0f, 1f)))

        // Models
//        val gridModelId = engineCore.resources.addModel(gridMesh)
        val coneModelId = engineCore.resources.addModel(coneMesh)
        val boxModelId = engineCore.resources.loadModel("models/orientationBox.obj")
        val tombstoneModelId = engineCore.resources.loadModel("models/tombstone.obj")


        val random = Random()
        val objectsCount = 60_000 // 30_000
        val translateLimit = objectsCount / 140 // 140

        val renderComponentA = RenderComponent.build(boxModelId, transparentMaterialId)
        val renderComponentB = RenderComponent.build(tombstoneModelId, tombstoneMaterialId)
        val renderComponentC = RenderComponent.build(boxModelId, simpleMaterialId)

        engineCore.buffer.addEntity(CameraController())
//        engineCore.buffer.addEntity(TestEntity())

        engineCore.buffer.addEntity(Entity(
                Matrix4().idt(),
                RenderComponent.build(boxModelId, centerMaterialId)
        ))

//        engineCore.buffer.addEntity(RenderableEntity(
//                RenderComponent.build(coneModelId, lineMaterialId)
//        ))

        Log.info("adding objects ..")

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

//        engineCore.buffer.addEntity(RenderableEntity(renderComponentC))

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