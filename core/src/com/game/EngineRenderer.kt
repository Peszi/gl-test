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
): RenderInterface {

    var gameCamera: GameCamera = GameCamera()
    private val prefs = RenderingPrefs()
    private val lock = java.lang.Object()

    // Stats

    val diagnosticTimer = DiagnosticTimer()
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

    override fun requestFrame() {
        synchronized(lock) { lock.notifyAll() }
    }

    fun render(entitiesList: List<Entity>) {
        synchronized(lock) { lock.wait() }
        diagnosticTimer.startTimer()
        update(Gdx.graphics.deltaTime)
        elapsedTime += Gdx.graphics.deltaTime
        // Clean up
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
        // Render
        entitiesList.forEach(this::renderEntities)
        prefs.reset()
        Gdx.graphics.requestRendering()
        diagnosticTimer.stopTimer()
    }

    fun printDiag() {
        println("switches per frame: material = $materialSwitches mesh = $meshSwitches")
        materialSwitches = 0; meshSwitches = 0
    }

    private fun renderEntities(entity: Entity) {
        val materialId = entity.renderable.materialId
        if (prefs.currentMaterialId != materialId) {
            materialSwitches++
            prefs.currentMaterial = engineResources.getMaterial(materialId)
            prefs.currentMaterialId = materialId

            val textureId = prefs.currentMaterial!!.textureId
            if (prefs.currentTextureId != textureId) {
                prefs.currentTexture = engineResources.getTexture(prefs.currentMaterial!!.textureId)
                prefs.currentTexture!!.bind()
                prefs.currentTextureId = textureId
            }

            val shaderId = prefs.currentMaterial!!.shaderId
            if (prefs.currentShaderId != shaderId) {
                if (prefs.currentShaderId > 0) prefs.currentShader!!.end()
                prefs.currentShader = engineResources.getShader(prefs.currentMaterial!!.shaderId)
                prefs.currentShader!!.begin()
                prefs.currentShader!!.setUniformMatrix("u_projTrans", gameCamera.camera.combined)
                prefs.currentShader!!.setUniformf("u_cameraPos", gameCamera.camera.position)
                prefs.currentShader!!.setUniformf("u_time", elapsedTime * 10f)
                prefs.currentShaderId = shaderId
            }

            prefs.currentShader!!.setUniformf("u_color", prefs.currentMaterial!!.color)
            prefs.currentShader!!.setUniformi("u_texture", 0)
        }

        val meshId = entity.renderable.meshId
        if (prefs.currentMeshId != meshId) {
            meshSwitches++
            if (prefs.currentMeshId > 0) prefs.currentMesh!!.unbind(prefs.currentShader)
            prefs.currentMesh = engineResources.getModel(entity.renderable.meshId)
            prefs.currentMesh!!.bind(prefs.currentShader)
            prefs.currentMeshId = meshId
        }

        prefs.currentShader!!.setUniformMatrix("u_worldTrans", entity.transform)
        prefs.currentMesh!!.render(prefs.currentShader!!, GL20.GL_TRIANGLES)
    }

}

internal interface RenderInterface {
    fun requestFrame()
}

internal class RenderingPrefs {

    var currentMaterial: MaterialResource? = null
    var currentMesh: Mesh? = null
    var currentShader: ShaderProgram? = null
    var currentTexture: Texture? = null

    var currentMaterialId: Int = -1
    var currentMeshId: Int = -1
    var currentShaderId: Int = -1
    var currentTextureId: Int = -1

    fun reset() {
        currentMaterialId = -1; currentMeshId = -1
        currentShaderId = -1; currentTextureId = -1
    }

}