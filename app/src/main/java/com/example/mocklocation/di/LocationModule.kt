package com.example.mocklocation.di

import com.example.mocklocation.data.datasource.LocationDataSource
import com.example.mocklocation.data.datasource.LocationDataSourceImpl
import com.example.mocklocation.data.repository.LocationRepositoryImpl
import com.example.mocklocation.domain.repository.LocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {
    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        impl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    abstract fun bindLocationDataSource(
        impl: LocationDataSourceImpl
    ): LocationDataSource
}