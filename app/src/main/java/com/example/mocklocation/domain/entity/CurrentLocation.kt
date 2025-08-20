package com.example.mocklocation.domain.entity

data class CurrentLocation(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float?,
    val bearing: Float?
)