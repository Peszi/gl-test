package com.game.entity

import com.badlogic.gdx.math.Matrix4
import com.game.core.EngineCore

internal open class Entity(
        var transform: Matrix4 = Matrix4().idt(),
        var renderable: RenderComponent? = null
) {

    open fun start(engineCore: EngineCore) {}

    open fun update(delta: Float, engineCore: EngineCore) {}

}