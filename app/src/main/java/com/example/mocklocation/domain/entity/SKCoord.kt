package com.example.mocklocation.domain.entity

data class SKCoord(val x: Double, val y: Double) {
    companion object {
        private const val SCALE = 360000.0
        fun fromVertex(x: Int, y: Int, origin: TileOrigin): SKCoord {
            return SKCoord(origin.x * SCALE + x, origin.y * SCALE + y)
        }
    }
}