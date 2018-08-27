package com.game.entity

import com.badlogic.gdx.math.Matrix4
import com.game.core.EngineCore

internal open abstract class Entity(
        var transform: Matrix4 = Matrix4().idt()
) {

    open fun start(engineCore: EngineCore) {}

    abstract fun update(delta: Float, engineCore: EngineCore)

}