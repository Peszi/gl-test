package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.graphics.glutils.ShaderProgram

internal interface EngineResources {
    fun loadShader(shaderName: String)
    fun loadTexture(textureName: String)
    fun loadModel(modelName: String)
    fun getShader(shaderName: String): ShaderProgram?
    fun getTexture(textureName: String): Texture?
    fun getModel(modelName: String): Model?
    fun reloadResources()
    fun disposeResources()
}

internal class EngineResourcesImpl: EngineResources {

    val shadersBuffer = mutableMapOf<String, ShaderProgram>()
    val texturesBuffer = mutableMapOf<String, Texture>()
    val modelsBuffer = mutableMapOf<String, Model>()

    private lateinit var objLoader: ObjLoader

    init {
        objLoader = ObjLoader()
    }

    override fun loadShader(shaderName: String) {
        println("loading shader [$shaderName]")
        shadersBuffer[shaderName]?.dispose()
        shadersBuffer[shaderName] = ShaderProgram(
                Gdx.files.internal("${SHADERS_DIR + shaderName}Vertex.glsl").readString(),
                Gdx.files.internal("${SHADERS_DIR + shaderName}Fragment.glsl").readString()
        )
    }

    override fun loadTexture(textureName: String) {
        println("loading texture [$textureName]")
        texturesBuffer[textureName]?.dispose()
        texturesBuffer[textureName] = Texture(TEXTURES_DIR + textureName)
    }

    override fun loadModel(modelName: String) {
        println("loading model [$modelName]")
        modelsBuffer[modelName]?.dispose()
        var model = objLoader.loadModel(Gdx.files.internal(MODELS_DIR + modelName))
        model.meshes.forEach{ it.setAutoBind(false) }
        modelsBuffer[modelName] = model
}

    override fun getShader(shaderName: String) = shadersBuffer[shaderName]

    override fun getTexture(textureName: String)= texturesBuffer[textureName]

    override fun getModel(modelName: String) = modelsBuffer[modelName]

    override fun reloadResources() { reloadShaders(); reloadTextures() }

    override fun disposeResources() {
        shadersBuffer.values.forEach(ShaderProgram::dispose)
        texturesBuffer.values.forEach(Texture::dispose)
    }

    private fun reloadShaders() { shadersBuffer.keys.forEach(this::loadShader) }

    private fun reloadTextures() { texturesBuffer.keys.forEach(this::loadTexture) }

    companion object {
        const val SHADERS_DIR = "shaders/"
        const val TEXTURES_DIR = "textures/"
        const val MODELS_DIR = "models/"
    }

}