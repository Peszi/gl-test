package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.Shader
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.math.Vector3

internal interface EngineResources {
    fun loadShader(shaderName: String): Int
    fun loadTexture(textureName: String): Int
    fun loadModel(modelName: String): Int
    fun addMaterial(material: MaterialResource): Int

    fun getShader(shaderId: Int): ShaderProgram
    fun getTexture(textureId: Int): Texture
    fun getModel(modelId: Int): Mesh
    fun getMaterial(materialId: Int): MaterialResource

    fun disposeResources()
}

internal class EngineResourcesImpl: EngineResources {

    private val shadersBuffer = mutableListOf<ShaderProgram>()
    private val texturesBuffer = mutableListOf<Texture>()
    private val materialsBuffer = mutableListOf<MaterialResource>()

    private val modelsBuffer = mutableListOf<Mesh>()

    private var objLoader = ObjLoader()

    override fun loadShader(shaderName: String): Int {
        println("loading shader [$shaderName]")
        val shaderProgram = ShaderProgram(
                Gdx.files.internal("${SHADERS_DIR + shaderName}Vertex.glsl").readString(),
                Gdx.files.internal("${SHADERS_DIR + shaderName}Fragment.glsl").readString()
        )
        if (!shaderProgram.isCompiled)
            throw RuntimeException(shaderProgram.log)
        shaderProgram.uniforms.map { " - $it" }.forEach(System.out::println)
        shadersBuffer.add(shaderProgram)
        return shadersBuffer.size-1
    }

    override fun loadTexture(textureName: String): Int {
        println("loading texture [$textureName]")
        texturesBuffer.add(Texture(Gdx.files.internal(TEXTURES_DIR + textureName), true))
        return texturesBuffer.size-1
    }

    override fun loadModel(modelName: String): Int {
        println("loading model [$modelName]")
        val mesh = objLoader.loadModel(Gdx.files.internal(MODELS_DIR + modelName)).meshes.first()
        mesh.setAutoBind(false)
        modelsBuffer.add(mesh)
        return modelsBuffer.size-1
    }

    override fun addMaterial(material: MaterialResource): Int {
        println("prepaing material!")
        materialsBuffer.add(material)
        return materialsBuffer.size-1
    }

    override fun getShader(shaderId: Int) =
            shadersBuffer.getOrNull(shaderId) ?: throw RuntimeException("Shader not exists!")

    override fun getTexture(textureId: Int) =
            texturesBuffer.getOrNull(textureId) ?: throw RuntimeException("Texture not exists!")

    override fun getModel(modelId: Int) =
            modelsBuffer.getOrNull(modelId) ?: throw RuntimeException("Model not exists!")

    override fun getMaterial(materialId: Int) =
            materialsBuffer.getOrNull(materialId) ?: throw RuntimeException("Material not exists!")

    override fun disposeResources() {
        shadersBuffer.forEach(ShaderProgram::dispose)
        texturesBuffer.forEach(Texture::dispose)
    }

    companion object {
        const val SHADERS_DIR = "shaders/"
        const val TEXTURES_DIR = "textures/"
        const val MODELS_DIR = "models/"
    }

}

internal class MaterialResource(
        var shaderId: Int = 0,
        var textureId: Int = 0,
        var color: Color = Color(1f, 1f, 1f, 1f)
)