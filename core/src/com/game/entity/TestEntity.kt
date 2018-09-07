package com.game.entity

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Vector3
import com.game.core.EngineCore
import com.game.diag.Log
import java.util.*

internal class TestEntity: Entity() {

    val random = Random()
    var time = 0f

    override fun start(engineCore: EngineCore) {
        super.start(engineCore)

        engineCore.buffer.addEntity(Entity(
                Matrix4().idt(),
                RenderComponent.build(2, 2 to engineCore.resources.getMaterial(2))
        ))
    }

    override fun update(delta: Float, engineCore: EngineCore) {

        time += delta
        Log.info("value " + time)
        transform.setToTranslation(0f, time, 0f)
        if (time >= 5f) {
            time = 0f
//            Log.info("Adding entity!")
//            val entity = RenderableEntity(RenderComponent.build(2, engineCore.resources.getDefaultMaterial()))
//            entity.transform.translate(Vector3(random.nextFloat() * 15f, 0f, random.nextFloat() * 15f))
//            engineCore.buffer.addEntity(entity)
        }
    }
}