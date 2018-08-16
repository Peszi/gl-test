package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.game.util.EntitiesMap

internal class EngineRenderer(
        val engineResources: EngineResources
) {

    private val entitiesList = mutableListOf<Entity>()
    private val entitiesKeys = mutableListOf<Int>()

    // tmp

    private var currentMaterial: MaterialResource? = null
    private var currentMesh: Mesh? = null
    private var currentShader: ShaderProgram? = null
    private var currentTexture: Texture? = null

    var camera = PerspectiveCamera(
            75f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()
    )

    init {
        camera.position.set(0f, 0f, 4f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 0.1f
        camera.far = 2000.0f
        camera.update()
    }

    fun addEntity(entity: Entity) {
        entitiesList.add(entity)
        entitiesKeys.add(RenderUtil.generateKey(entity, camera))
    }

    fun update() {
        val delta = Gdx.graphics.deltaTime
        entitiesList.forEach { it.update(delta) }
    }

    fun render() {
        // update keys
        entitiesKeys.mapIndexed { index, _ -> RenderUtil.generateKey(entitiesList[index], camera) }
        // sort entities
        EntitiesMap.build(entitiesKeys).getSortedEntities().forEach(this::renderEntities)
    }

    private fun renderEntities(entitiesGroup: MutableList<Int>) {
        entitiesGroup.forEachIndexed { index, i ->
            val entity = entitiesList.getOrNull(i) ?: return@forEachIndexed
            if (index == 0) {
                currentMaterial = engineResources.getMaterial(entity.renderable.materialId)
                currentMesh = engineResources.getModel(entity.renderable.meshId)
                currentShader = engineResources.getShader(currentMaterial!!.shaderId)
                currentTexture = engineResources.getTexture(currentMaterial!!.textureId)

                currentTexture!!.bind()
                currentMesh!!.bind(currentShader)
                currentShader!!.begin()
                currentShader!!.setUniformMatrix("u_projTrans", camera.combined)
                currentShader!!.setUniformf("u_cameraPos", camera.position)

                currentShader!!.setUniformf("u_color", currentMaterial!!.color)
                currentShader!!.setUniformi("u_texture", 0)
            }
            renderEntity(entity.transform)
            if(index == entitiesGroup.size - 1) {
                currentShader!!.end()
                currentMesh!!.unbind(currentShader)
            }
        }
    }

    private fun renderEntity(transform: Matrix4) {
        currentShader!!.setUniformMatrix("u_worldTrans", transform)
        currentMesh!!.render(currentShader!!, GL20.GL_TRIANGLES)
    }

}


//        texture.bind()
//
//        shader.begin()
//
//        shader.setUniformMatrix("u_projTrans", camera.combined)
//        shader.setUniformf("u_color", 1f, 1f, 1f, 1f)
//        shader.setUniformi("u_texture", 0)
//
//        val objectsCount = 50
//
//        model.meshes.forEach {
//            it.bind(shader)
//            for (x in 0..objectsCount) {
//                for (y in 0..objectsCount) {
//                    transform.setTranslation(-objectsCount * 5f + x * 5f, 0f, -objectsCount * 5f + y * 5f)
//                    shader.setUniformMatrix("u_worldTrans", transform)
//                    it.renderable(shader, GL20.GL_TRIANGLES)
//                }
//            }
//            it.unbind(shader)
//        }
//        shader.end()