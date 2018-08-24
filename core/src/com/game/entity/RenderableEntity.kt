package com.game.entity

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.game.core.EngineCore
import com.game.render.RenderUtil

internal open class RenderableEntity(
        var renderable: RenderComponent = RenderComponent()
): Entity() {

    override fun input(delta: Float, engineCore: EngineCore) {}

    override fun update(delta: Float) {}
}

internal class RenderComponent(
        var meshId: Int = 0,
        var materialId: Int = 0,
        var renderingKey: Long = 0L
) {

    companion object {

        fun build(meshId: Int, material: Pair<Int, MaterialResource>) = RenderComponent(
                meshId, material.first,
                RenderUtil.generateKey(material)
        )
    }
}

internal class MaterialResource(
        var shaderId: Int = 0,
        var textureId: Int = 0,
        var color: Color = Color(1f, 1f, 1f, 1f)
) {

    fun isTransparent() = color.a < 1f

}