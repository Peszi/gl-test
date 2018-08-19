package com.game

internal object RenderUtil {

    private const val THREE_BYTES_MASK: Long = 0xffffff

    private const val TRANSPARENCY_OFFSET = 48
    private const val LOWER_BUFFER_OFFSET = 0
    private const val HIGHER_BUFFER_OFFSET = 24

    fun generateKey(material: Pair<Int, MaterialResource>): Long {
        var generatedKey = 0L
        val isTransparent = material.second.isTransparent()
        // transparency
        generatedKey = generatedKey or
                (if (isTransparent) 0 else 1).toLong().shl(TRANSPARENCY_OFFSET)
        // materialID
        generatedKey = generatedKey or
                (material.first.toLong() and THREE_BYTES_MASK).shl(getMaterialOffset(isTransparent))

        return generatedKey
    }

    private fun getMaterialOffset(isTransparent: Boolean): Int =
            if (isTransparent) LOWER_BUFFER_OFFSET else HIGHER_BUFFER_OFFSET

    private fun getDepthOffset(isTransparent: Boolean): Int =
            if (isTransparent) HIGHER_BUFFER_OFFSET else LOWER_BUFFER_OFFSET

    private fun isTransparent(renderingKey: Long): Boolean {
        val mask = 1L shl TRANSPARENCY_OFFSET
        return ((renderingKey and mask) != mask)
    }

    fun getRenderKey(currentKey: Long, entityDepth: Float): Long {
        var depth: Long = (entityDepth * THREE_BYTES_MASK).toLong()
        val isTransparent = RenderUtil.isTransparent(currentKey)
//        // entityDepth
        if (!isTransparent) depth = 0x0 // depth = THREE_BYTES_MASK - depth
        val value = depth.shl(getDepthOffset(isTransparent))
        return (currentKey or value)
    }

}