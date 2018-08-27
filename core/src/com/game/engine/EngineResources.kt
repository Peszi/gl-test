package com.game.engine

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Mesh
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.game.diag.Log
import com.game.entity.MaterialResource
import java.util.*

internal interface EngineResources {
    fun loadShader(shaderName: String): Int
    fun loadTexture(textureName: String): Int
    fun loadModel(modelName: String): Int
    fun addModel(mesh: Mesh): Int
    fun addMaterial(material: MaterialResource): Pair<Int, MaterialResource>

    fun getDefaultShader(): Int
    fun getDefaultTexture(): Int
    fun getDefaultMaterial(): Pair<Int, MaterialResource>
    fun getDefaultModel(): Int

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

    private val modelsBuffer = mutableListOf<Mesh>() //Collections.synchronizedList(())

    private var objLoader = ObjLoader()

    override fun loadShader(shaderName: String): Int {

        Log.info("loading shader [$shaderName]")
        val shaderProgram = ShaderProgram(
                Gdx.files.internal("${shaderName}Vertex.glsl").readString(),
                Gdx.files.internal("${shaderName}Fragment.glsl").readString()
        )
        if (!shaderProgram.isCompiled)
            throw RuntimeException(shaderProgram.log)
//        shaderProgram.uniforms.map { " - $it" }.forEach(System.out::println) // printing all shader uniforms
        shadersBuffer.add(shaderProgram)
        return shadersBuffer.size-1
    }

    override fun loadTexture(textureName: String): Int {
        Log.info("loading texture [$textureName]")
        texturesBuffer.add(Texture(Gdx.files.internal(textureName), true))
        return texturesBuffer.size-1
    }

    override fun loadModel(modelName: String): Int {
        Log.info("loading model [$modelName]")
        val mesh = objLoader.loadModel(Gdx.files.internal(modelName)).meshes.first()
        mesh.setAutoBind(false)
        modelsBuffer.add(mesh)
        return modelsBuffer.size-1
    }

    override fun addModel(mesh: Mesh): Int {
        mesh.setAutoBind(false)
        modelsBuffer.add(mesh)
        Log.info("loading model ID ${modelsBuffer.size-1}")
        return modelsBuffer.size-1
    }

    override fun addMaterial(material: MaterialResource): Pair<Int, MaterialResource> {
        materialsBuffer.add(material)
        Log.info("preparing material ID: ${materialsBuffer.size-1}")
        return materialsBuffer.size-1 to material
    }

    override fun getDefaultShader(): Int = 0

    override fun getDefaultTexture(): Int = 0

    override fun getDefaultMaterial(): Pair<Int, MaterialResource> = 0 to materialsBuffer[0]

    override fun getDefaultModel(): Int = 0

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

}