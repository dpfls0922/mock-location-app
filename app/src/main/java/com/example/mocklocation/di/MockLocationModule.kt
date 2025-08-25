package com.example.mocklocation.di

import com.example.mocklocation.data.repository.MockLocationRepositoryImpl
import com.example.mocklocation.domain.repository.MockLocationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MockLocationModule {
    @Binds
    abstract fun bindMockLocationRepository(
        mockLocationRepositoryImpl: MockLocationRepositoryImpl
    ): MockLocationRepository
}