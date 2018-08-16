package com.game

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import java.util.*

internal class Entity(
        var transform: Matrix4 = Matrix4().idt(),
        var renderable: RenderComponent = RenderComponent(),
        random: Random
) {

    private val direction = Vector3(
            (random.nextFloat() - .5f) * 2,
            (random.nextFloat() - .5f) * 2,
            (random.nextFloat() - .5f) * 2
    ).nor()

    var tmp = Vector3()

    fun update(delta: Float) {
        transform.translate(tmp.set(direction).scl(delta * 0.1f))
    }
}

internal class RenderComponent(
        var meshId: Int = 0,
        var materialId: Int = 0
)