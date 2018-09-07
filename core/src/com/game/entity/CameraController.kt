package com.game.entity

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.game.core.EngineCore
import com.game.diag.Log

internal class CameraController: Entity() {

    private  val cameraUp = Vector3(0f, 1f, 0f)

    private var velocity: Vector3 = Vector3()

    private var vector: Vector3 = Vector3()
    private var tmp = Vector3()

    private var freeCamera = false

    override fun start(engineCore: EngineCore) {
        renderable = RenderComponent.build(0, engineCore.resources.getDefaultMaterial())
    }

    override fun update(delta: Float, engineCore: EngineCore) {
        // Rotation
        val direction = engineCore.mainCamera.camera.direction
        Gdx.input.isCursorCatched = Gdx.input.isButtonPressed(Input.Buttons.LEFT)
        if (Gdx.input.isCursorCatched) {
            val deltaX = -Gdx.input.deltaX * LOOK_SPEED
            val deltaY = -Gdx.input.deltaY * LOOK_SPEED
            direction.rotate(cameraUp, deltaX)
            direction.rotate(tmp.set(direction).crs(cameraUp).nor(), deltaY)
        }
        // Movement
        val position = engineCore.mainCamera.camera.position
        vector = Vector3.Zero
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            vector.add(direction)
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            vector.add(tmp.set(direction).scl(-1f))
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            vector.add(tmp.set(direction).crs(cameraUp).scl(-1f))
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            vector.add(tmp.set(direction).crs(cameraUp))
        if (Gdx.input.isKeyPressed(Input.Keys.R))
            vector.add(cameraUp)
        if (Gdx.input.isKeyPressed(Input.Keys.F))
            vector.add(tmp.set(cameraUp).scl(-1f))
        // Add velocity
        velocity.add(vector.scl(delta * MOVE_SPEED))
        position.add(velocity)
        velocity.scl(.89f)
        // Update mainCamera cone
//        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) freeCamera = !freeCamera
        if (engineCore.mainCamera.updateFrustum) {
            transform = engineCore.mainCamera.camera.view.cpy()
            Matrix4.inv(transform.values)
        }
    }

    companion object {
        const val LOOK_SPEED = 0.1f
        const val MOVE_SPEED = 2.0f
    }
}