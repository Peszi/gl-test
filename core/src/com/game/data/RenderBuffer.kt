package com.game.data

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.game.entity.RenderComponent

internal class RenderBuffer(
        val renderPrefs: RenderPrefs,
        val renderableList: List<RenderData>
)

internal class RenderPrefs(
        val cameraPosition: Vector3,
        val cameraDirection: Vector3,

        val elapsedTime: Float
)

internal class RenderData(
        val transform: Matrix4,
        val renderable: RenderComponent
)