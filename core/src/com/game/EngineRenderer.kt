package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.game.util.EntitiesMap

internal class EngineRenderer(
        private val engineResources: EngineResources
) {

    private var gameCamera: GameCamera = GameCamera()

    private var currentMaterial: MaterialResource? = null
    private var currentMesh: Mesh? = null
    private var currentShader: ShaderProgram? = null
    private var currentTexture: Texture? = null

    private var currentMaterialId: Int = -1
    private var currentMeshId: Int = -1
    private var currentShaderId: Int = -1
    private var currentTextureId: Int = -1

    // tmp

    var materialSwitches: Int = 0
    var meshSwitches: Int = 0

    var camera = PerspectiveCamera(
            60f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()
    )

    init {
        camera.position.set(0f, 0f, 4f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 0.1f
        camera.far = 2000.0f
        camera.update()
    }

    fun render(entitiesOrder: List<MutableList<Int>>, entitiesList: List<Entity>) {

        gameCamera.update(camera, Gdx.graphics.deltaTime)

        // Setup
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

        // Render
        entitiesOrder.forEach { renderEntities(it, entitiesList) }

        resetBindings()
//        printDiag()
    }

    fun printDiag() {
        println("switches per frame: material = $materialSwitches mesh = $meshSwitches")
        materialSwitches = 0; meshSwitches = 0
    }

    private fun renderEntities(entitiesOrder: MutableList<Int>, entitiesList: List<Entity>) {
        entitiesOrder.forEach {
            val entity = entitiesList.getOrNull(it) ?: return

            val materialId = entity.renderable.materialId
            if (currentMaterialId != materialId) {
                materialSwitches++
                currentMaterial = engineResources.getMaterial(materialId)
                currentMaterialId = materialId

                val textureId = currentMaterial!!.textureId
                if (currentTextureId != textureId) {
                    currentTexture = engineResources.getTexture(currentMaterial!!.textureId)
                    currentTexture!!.bind()
                    currentTextureId = textureId
                }

                val shaderId = currentMaterial!!.shaderId
                if (currentShaderId != shaderId) {
                    if (currentShaderId > 0) currentShader!!.end()
                    currentShader = engineResources.getShader(currentMaterial!!.shaderId)
                    currentShader!!.begin()
                    currentShader!!.setUniformMatrix("u_projTrans", camera.combined)
                    currentShader!!.setUniformf("u_cameraPos", camera.position)
                    currentShaderId = shaderId
                }

                currentShader!!.setUniformf("u_color", currentMaterial!!.color)
                currentShader!!.setUniformi("u_texture", 0)
            }

            val meshId = entity.renderable.meshId
            if (currentMeshId != meshId) {
                meshSwitches++
                if (currentMeshId > 0) currentMesh!!.unbind(currentShader)
                currentMesh = engineResources.getModel(entity.renderable.meshId)
                currentMesh!!.bind(currentShader)
                currentMeshId = meshId
            }

            currentShader!!.setUniformMatrix("u_worldTrans", entity.transform)
            currentMesh!!.render(currentShader!!, GL20.GL_TRIANGLES)
        }
    }

    private fun resetBindings() {
        currentMaterialId = -1; currentMeshId = -1
        currentShaderId = -1; currentTextureId = -1
    }

}