package com.game

import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.math.Matrix4

internal class Entity(
        var transform: Matrix4 = Matrix4().idt(),
        var render: RenderComponent = RenderComponent()
)

internal class RenderComponent(
        var shaderName: String = DEFAULT_SHADER,
        var textureName: String = DEFAULT_TEXTURE,
        var modelName: String = DEFAULT_MODEL
) {


    companion object {
        const val DEFAULT_SHADER = "simple"
        const val DEFAULT_TEXTURE = "texture.png"
        const val DEFAULT_MODEL = ""
    }
}