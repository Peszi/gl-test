package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Matrix4

internal class EngineRenderer {

    val entitiesList = mutableListOf<Entity>()

    // tmp

    var camera = PerspectiveCamera(
            75f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()
    )

    init {
        camera.position.set(0f, 0f, 4f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 0.1f
        camera.far = 1300.0f
        camera.update()
    }

    fun render(engineResources: EngineResources) {
        // sort entities
        entitiesList.forEach {
            renderEntity(
                    it.transform,
                    engineResources.getShader(it.render.shaderName)!!,
                    engineResources.getTexture(it.render.textureName)!!,
                    engineResources.getModel(it.render.modelName)!!
            )
        }
    }

    private fun renderEntity(transform: Matrix4, shader: ShaderProgram, texture: Texture, model: Model) {
        texture.bind()

        shader.begin()

        shader.setUniformMatrix("u_projTrans", camera.combined)
        shader.setUniformf("u_color", 1f, 1f, 1f, 1f)
        shader.setUniformi("u_texture", 0)

        model.meshes.forEach {
            it.bind(shader)
            shader.setUniformMatrix("u_worldTrans", transform)
            it.render(shader, GL20.GL_TRIANGLES)
            it.unbind(shader)
        }
        shader.end()
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
//                    it.render(shader, GL20.GL_TRIANGLES)
//                }
//            }
//            it.unbind(shader)
//        }
//        shader.end()