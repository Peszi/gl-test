package com.game.entity

import com.badlogic.gdx.math.Vector3
import com.game.core.EngineCore
import java.util.*

internal class KinematicEntity(
        random: Random,
        position: Vector3,
        renderable: RenderComponent
): RenderableEntity(
        renderable
) {

    init {
        transform.setToTranslation(position)
        transform.rotate(Vector3(random.nextFloat(), random.nextFloat(), random.nextFloat()), random.nextFloat() * 360)
    }

    private val direction = transform.getTranslation(Vector3()).nor()
    private var tmp = Vector3()

    override fun input(delta: Float, engineCore: EngineCore) {

    }

    override fun update(delta: Float) {
        transform.translate(tmp.set(direction).scl(delta * .002f))
    }
}