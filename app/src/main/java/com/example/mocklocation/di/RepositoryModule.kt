package com.example.mocklocation.di

import com.example.mocklocation.data.repository.GpsRepositoryImpl
import com.example.mocklocation.domain.repository.GpsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindToGpsRepository(gpsRepositoryImpl: GpsRepositoryImpl): GpsRepository
}