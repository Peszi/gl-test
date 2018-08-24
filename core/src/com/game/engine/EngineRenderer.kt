package com.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.game.data.BlockingBuffer
import com.game.data.RenderBuffer
import com.game.diag.DiagTimer
import com.game.entity.MaterialResource
import com.game.diag.Diagnostic
import com.game.render.GameCamera
import java.util.*
import kotlin.concurrent.schedule

internal class EngineRenderer(
        private val engineResources: EngineResources,
        private val renderBuffer: BlockingBuffer<RenderBuffer>,
        private val diagnostic: Diagnostic
) {

    private var gameCamera: GameCamera = GameCamera()
    private val prefs = RenderingSettings()

    // Stats

    var materialSwitches: Int = 0
    var meshSwitches: Int = 0

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

        Gdx.graphics.isContinuousRendering = false
        Gdx.graphics.requestRendering()

        Timer().schedule(0, 1000){ printDiag() }
    }

    fun resize(width: Float, height: Float) {
        gameCamera.updateViewport(width, height)
    }

    fun render() {
        renderBuffer.processData {
            val startTime = DiagTimer.getTimeStamp()
            // Clean frame
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)
            // Render
            gameCamera.updateCamera(this.renderPrefs.cameraPosition, this.renderPrefs.cameraDirection)
            renderEntities(this)
            // Clean up
            prefs.reset()
            Gdx.graphics.requestRendering()
            diagnostic.onRenderEnd(startTime, DiagTimer.getTimeStamp())
        }
    }

    private fun renderEntities(buffer: RenderBuffer) {
        buffer.renderableList.forEach {
            val materialId = it.renderable.materialId
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
                    prefs.currentShader!!.setUniformf("u_time", buffer.renderPrefs.elapsedTime * 10f)
                    prefs.currentShaderId = shaderId
                }

                prefs.currentShader!!.setUniformf("u_color", prefs.currentMaterial!!.color)
                prefs.currentShader!!.setUniformi("u_texture", 0)
            }

            val meshId = it.renderable.meshId
            if (prefs.currentMeshId != meshId) {
                meshSwitches++
                if (prefs.currentMeshId > 0) prefs.currentMesh!!.unbind(prefs.currentShader)
                prefs.currentMesh = engineResources.getModel(it.renderable.meshId)
                prefs.currentMesh!!.bind(prefs.currentShader)
                prefs.currentMeshId = meshId
            }

            prefs.currentShader!!.setUniformMatrix("u_worldTrans", it.transform)
            prefs.currentMesh!!.render(prefs.currentShader!!, GL20.GL_TRIANGLES)
        }
    }

    fun printDiag() {
        println("switches per frame: material = $materialSwitches mesh = $meshSwitches")
        materialSwitches = 0; meshSwitches = 0
    }
}

internal class RenderingSettings {

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