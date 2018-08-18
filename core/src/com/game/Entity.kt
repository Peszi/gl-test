package com.game

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import java.util.*

internal class Entity(
        var transform: Matrix4 = Matrix4().idt(),
        var renderable: RenderComponent = RenderComponent(),
        var isStatic: Boolean = false
) {

    private val direction = transform.getTranslation(Vector3()).nor()

    var tmp = Vector3()

    fun update(delta: Float) {
        if (!isStatic)
            transform.translate(tmp.set(direction).scl(delta * 0.1f))
    }

    fun clone() =
            Entity(transform, renderable, isStatic)

    companion object {
        fun build() {}
    }
}

internal class RenderComponent(
        var meshId: Int = 0,
        var materialId: Int = 0,
        var renderingKey: Long = 0L,
        var tranparant: Boolean = false
) {

    companion object {

        fun build(meshId: Int, material: Pair<Int, MaterialResource>) = RenderComponent(
                meshId, material.first,
                RenderUtil.generateKey(material),
                material.second.isTransparent()
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