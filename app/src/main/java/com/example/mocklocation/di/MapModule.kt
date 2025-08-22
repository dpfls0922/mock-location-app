package com.example.mocklocation.di

import com.example.mocklocation.data.service.MapServiceImpl
import com.example.mocklocation.domain.service.MapService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapModule {
    @Binds
    @Singleton
    abstract fun bindMapService(
        vsmMapService: MapServiceImpl
    ): MapService
}