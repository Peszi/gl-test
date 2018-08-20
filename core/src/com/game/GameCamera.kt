package com.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.PerspectiveCamera
import com.badlogic.gdx.math.Vector3

internal class GameCamera {

    companion object {
        const val LOOK_SPEED = 0.1f
        const val MOVE_SPEED = 2.0f
    }

    private var velocity: Vector3 = Vector3()
    private var vector: Vector3 = Vector3()

    private var tmp = Vector3()

    var camera = PerspectiveCamera(
            60f, Gdx.graphics.width.toFloat(), Gdx.graphics.height.toFloat()
    )

    init {
        camera.position.set(0f, 0f, 0f)
        camera.lookAt(0f, 0f, 0f)
        camera.near = 0.1f
        camera.far = 500.0f
        camera.update()
    }

    fun updateViewport(width: Float, height: Float) {
        camera.viewportWidth = width
        camera.viewportHeight = height
        Gdx.gl.glViewport(0, 0, camera.viewportWidth.toInt(), camera.viewportHeight.toInt())
    }

    fun update(deltaTime: Float) {
        // Rotation
        Gdx.input.isCursorCatched = Gdx.input.isButtonPressed(Input.Buttons.LEFT)
        if (Gdx.input.isCursorCatched) {
            val deltaX = -Gdx.input.deltaX * LOOK_SPEED
            val deltaY = -Gdx.input.deltaY * LOOK_SPEED
            camera.direction.rotate(camera.up, deltaX)
            camera.direction.rotate(tmp.set(camera.direction).crs(camera.up).nor(), deltaY)
        }
        // Movement
        vector = Vector3.Zero
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            vector.add(camera.direction)
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            vector.add(tmp.set(camera.direction).scl(-1f))
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            vector.add(tmp.set(camera.direction).crs(camera.up).scl(-1f))
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            vector.add(tmp.set(camera.direction).crs(camera.up))
        if (Gdx.input.isKeyPressed(Input.Keys.R))
            vector.add(camera.up)
        if (Gdx.input.isKeyPressed(Input.Keys.F))
            vector.add(tmp.set(camera.up).scl(-1f))
        // Add velocity
        velocity.add(vector.scl(deltaTime * MOVE_SPEED))
        camera.position.add(velocity)
        velocity.scl(.89f)

        camera.update()
    }
}