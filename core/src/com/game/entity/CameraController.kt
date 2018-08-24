package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector3
import com.game.core.EngineCore

internal class CameraController: Entity() {

    private  val cameraUp = Vector3(0f, 1f, 0f)

    private var velocity: Vector3 = Vector3()

    private var vector: Vector3 = Vector3()
    private var tmp = Vector3()

    override fun input(delta: Float, engineCore: EngineCore) {
        // Rotation
        Gdx.input.isCursorCatched = Gdx.input.isButtonPressed(Input.Buttons.LEFT)
        if (Gdx.input.isCursorCatched) {
            val deltaX = -Gdx.input.deltaX * LOOK_SPEED
            val deltaY = -Gdx.input.deltaY * LOOK_SPEED
            engineCore.direction.rotate(cameraUp, deltaX)
            engineCore.direction.rotate(tmp.set(engineCore.direction).crs(cameraUp).nor(), deltaY)
        }
        // Movement
        vector = Vector3.Zero
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            vector.add(engineCore.direction)
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            vector.add(tmp.set(engineCore.direction).scl(-1f))
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            vector.add(tmp.set(engineCore.direction).crs(cameraUp).scl(-1f))
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            vector.add(tmp.set(engineCore.direction).crs(cameraUp))
        if (Gdx.input.isKeyPressed(Input.Keys.R))
            vector.add(cameraUp)
        if (Gdx.input.isKeyPressed(Input.Keys.F))
            vector.add(tmp.set(cameraUp).scl(-1f))
        // Add velocity
        velocity.add(vector.scl(delta * MOVE_SPEED))
        engineCore.position.add(velocity)
        velocity.scl(.89f)
        // update camera
    }

    override fun update(deltaTime: Float) {}

    companion object {
        const val LOOK_SPEED = 0.1f
        const val MOVE_SPEED = 2.0f
    }
}