package com.game

import com.badlogic.gdx.math.Vector3

internal object RenderUtil {

    private const val THREE_BYTES_MASK = 0xffffff

    private const val TRANSPARENCY_OFFSET = 48
    private const val LOWER_BUFFER_OFFSET = 0
    private const val HIGHER_BUFFER_OFFSET = 24

    fun generateKey(material: Pair<Int, MaterialResource>): Long {
        var generatedKey = 0L
        val isTransparent = material.second.isTransparent()
        // transparency
        generatedKey = generatedKey or
                (if (isTransparent) 1 else 0) shl TRANSPARENCY_OFFSET
        // materialID
        generatedKey = generatedKey or
                (material.first and THREE_BYTES_MASK).toLong() shl getMaterialOffset(isTransparent)
        return generatedKey
    }

    private fun getMaterialOffset(isTransparent: Boolean): Int =
            if (isTransparent) LOWER_BUFFER_OFFSET else HIGHER_BUFFER_OFFSET

    private fun getDepthOffset(isTransparent: Boolean): Int =
            if (isTransparent) HIGHER_BUFFER_OFFSET else LOWER_BUFFER_OFFSET

    private fun isTransparent(renderingKey: Long): Boolean {
        val mask = 1L shl TRANSPARENCY_OFFSET
        return (renderingKey and mask == mask)
    }

    fun updateRenderKey(entity: Entity, camera: CameraPrefs): Long {
        var generatedKey = 0L
        var depth = camera.position.dst(entity.transform.getTranslation(Vector3())).toInt()
        val isTransparent = RenderUtil.isTransparent(entity.renderable.renderingKey)
        // depth
        if (!isTransparent) depth = Int.MAX_VALUE - depth
        generatedKey = generatedKey or
                (depth and THREE_BYTES_MASK).toLong() shl getDepthOffset(isTransparent)
        return entity.renderable.renderingKey or generatedKey
    }

}