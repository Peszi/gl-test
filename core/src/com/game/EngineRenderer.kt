package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.game.util.DiagnosticTimer
import java.util.*
import kotlin.concurrent.schedule

internal class EngineRenderer(
        private val engineResources: EngineResources
) {

    var gameCamera: GameCamera = GameCamera()

    private var currentMaterial: MaterialResource? = null
    private var currentMesh: Mesh? = null
    private var currentShader: ShaderProgram? = null
    private var currentTexture: Texture? = null

    private var currentMaterialId: Int = -1
    private var currentMeshId: Int = -1
    private var currentShaderId: Int = -1
    private var currentTextureId: Int = -1

    val diagnosticTimer = DiagnosticTimer()

    // Stats

    var materialSwitches: Int = 0
    var meshSwitches: Int = 0

    var elapsedTime = 0f

    init {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDepthMask(true)

        Gdx.gl.glEnable(GL20.GL_CULL_FACE)
        Gdx.gl.glCullFace(GL20.GL_BACK)
        Gdx.gl.glFrontFace(GL20.GL_CCW)

        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D)
        Gdx.gl20.glEnable(GL20.GL_BLEND)
        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

        Timer().schedule(0, 1000){ printDiag() }

        Gdx.graphics.isContinuousRendering = false
        Gdx.graphics.requestRendering()
    }

    fun resize(width: Float, height: Float) {
        gameCamera.updateViewport(width, height)
    }

    fun update(delta: Float) {
        gameCamera.update(Gdx.graphics.deltaTime)
    }

    fun render(entitiesList: List<Entity>) {
        diagnosticTimer.startTimer()
        update(Gdx.graphics.deltaTime)
        elapsedTime += Gdx.graphics.deltaTime
        // Clean up
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        // Render
        entitiesList.forEach(this::renderEntities)
        afterFrame()
        diagnosticTimer.stopTimer()
    }

    private fun afterFrame() {
        currentMaterialId = -1; currentMeshId = -1
        currentShaderId = -1; currentTextureId = -1
        Gdx.graphics.requestRendering()
    }

    fun printDiag() {
        println("switches per frame: material = $materialSwitches mesh = $meshSwitches")
        materialSwitches = 0; meshSwitches = 0
    }

    private fun renderEntities(entity: Entity) {
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
                currentShader!!.setUniformMatrix("u_projTrans", gameCamera.camera.combined)
                currentShader!!.setUniformf("u_cameraPos", gameCamera.camera.position)
                currentShader!!.setUniformf("u_time", elapsedTime * 10f)
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