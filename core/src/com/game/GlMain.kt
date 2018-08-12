package com.game

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.*
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g3d.*
import com.badlogic.gdx.graphics.glutils.ShaderProgram
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.math.Vector3
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.VertexAttribute
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.glutils.VertexBufferObject




class GlMain : ApplicationAdapter() {

    private lateinit var batch: SpriteBatch
    private var texture: Texture? = null
    private var shaderProgram: ShaderProgram? = null

    private lateinit var camera: Camera

    lateinit var modelBatch: ModelBatch
    lateinit var box: Model
    lateinit var boxInstance: ModelInstance

    lateinit var environment: Environment

    lateinit var mesh: Mesh
    lateinit var model: Model

    private var gameCamera: GameCamera = GameCamera()

    override fun create() {
        camera = PerspectiveCamera(75f,
                Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat())
        camera.position.set(0f, 0f, 4f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 0.1f
        camera.far = 1300.0f
        camera.update()

        modelBatch = ModelBatch()

        val objLoader = ObjLoader()

        model = objLoader.loadModel(Gdx.files.internal("orientationBox.obj"))
        model.meshes.forEach { it.setAutoBind(false) }

        batch = SpriteBatch()

        mesh = Mesh(true, 6, 0,
                VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
                VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0")
        )
        mesh.setVertices(initMesh())

        loadShaders()
        loadTextures()
    }

    private fun loadShaders() {
        println("loading shaders...")
        shaderProgram?.dispose()
        shaderProgram = ShaderProgram(
                Gdx.files.internal("shaders/vertexShader.glsl").readString(),
                Gdx.files.internal("shaders/fragmentShader.glsl").readString()
        )
    }

    private fun loadTextures() {
        println("loading textures...")
        texture?.dispose()
        texture = Texture("orientationBox.png")
    }

    fun update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F5)) loadShaders()
        gameCamera.update(camera, Gdx.graphics.deltaTime)
    }

    override fun render() {
        update()

        Gdx.gl.glViewport(0, 0, Gdx.graphics.width, Gdx.graphics.height)
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT or GL20.GL_DEPTH_BUFFER_BIT)


        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST)
        Gdx.gl.glDepthMask(false)
        Gdx.gl.glDisable(GL20.GL_BLEND)
        Gdx.gl.glEnable(GL20.GL_CULL_FACE)

        Gdx.gl20.glEnable(GL20.GL_TEXTURE_2D)
        Gdx.gl20.glEnable(GL20.GL_BLEND)
//        Gdx.gl20.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA)

//        shaderProgram?.let {
//            texture?.bind()
//
//            it.begin()
//            it.setUniformMatrix("u_projTrans", camera.combined)
//            it.setUniformi("u_texture", 0)
//
//            model.meshes.forEach {
//                it.render(shaderProgram, GL20.GL_TRIANGLES)
//            }
//            it.end()
//        }



        shaderProgram?.let {
            texture?.bind()

            it.begin()

            var transform = Matrix4().idt()

            shaderProgram!!.setUniformMatrix("u_projTrans", camera.combined)
            shaderProgram!!.setUniformi("u_texture", 0)

            val objectsCount = 250

            model.meshes.forEach {
                it.bind(shaderProgram)
                for (x in 0..objectsCount) {
                    for (y in 0..objectsCount) {
                        transform.setTranslation(-objectsCount * 5f + x * 5f, 0f, -objectsCount * 5f + y * 5f)
                        shaderProgram!!.setUniformMatrix("u_worldTrans", transform)
                        it.render(shaderProgram, GL20.GL_TRIANGLES)
                    }
                }
                it.unbind(shaderProgram)
            }

            shaderProgram!!.end()
        }
    }

    override fun dispose() {
        batch.dispose()
        texture?.dispose()
    }

    fun initMesh(): FloatArray {
        val verts = FloatArray(30)
        var i = 0
        val x: Float
        val y: Float // Mesh location in the world
        val width: Float
        val height: Float // Mesh width and height

        y = -2.5f
        x = y
        height = 5f
        width = height

        //Top Left Vertex Triangle 1
        verts[i++] = x   //X
        verts[i++] = y + height //Y
        verts[i++] = 0f    //Z
        verts[i++] = 0f   //U
        verts[i++] = 0f   //V

        //Top Right Vertex Triangle 1
        verts[i++] = x + width
        verts[i++] = y + height
        verts[i++] = 0f
        verts[i++] = 1f
        verts[i++] = 0f

        //Bottom Left Vertex Triangle 1
        verts[i++] = x
        verts[i++] = y
        verts[i++] = 0f
        verts[i++] = 0f
        verts[i++] = 1f

        //Top Right Vertex Triangle 2
        verts[i++] = x + width
        verts[i++] = y + height
        verts[i++] = 0f
        verts[i++] = 1f
        verts[i++] = 0f

        //Bottom Right Vertex Triangle 2
        verts[i++] = x + width
        verts[i++] = y
        verts[i++] = 0f
        verts[i++] = 1f
        verts[i++] = 1f

        //Bottom Left Vertex Triangle 2
        verts[i++] = x
        verts[i++] = y
        verts[i++] = 0f
        verts[i++] = 0f
        verts[i] = 1f
        return verts
    }
}