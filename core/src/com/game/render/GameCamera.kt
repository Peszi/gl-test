package com.game.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3

internal class GameCamera {

    companion object {
        const val CAMERA_FAR = 500.0f
    }

    var camera = PerspectiveCamera(
            60f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()
    )

    init {
        camera.position.set(0f, 0f, 0f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 0.1f
        camera.far = CAMERA_FAR
        camera.update()
    }

    fun updateViewport(width: Float, height: Float) {
        camera.viewportWidth = width
        camera.viewportHeight = height
        Gdx.gl.glViewport(0, 0, camera.viewportWidth.toInt(), camera.viewportHeight.toInt())
    }

    fun updateCamera(position: Vector3, direction: Vector3) {
        camera.position.set(position)
        camera.direction.set(direction)
        camera.update()
    }

}