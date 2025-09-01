package com.example.mocklocation.di

import com.example.mocklocation.data.datasource.TmapDataSource
import com.example.mocklocation.data.datasource.TmapDataSourceImpl
import com.example.mocklocation.data.repository.TmapRepositoryImpl
import com.example.mocklocation.domain.repository.TmapRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TmapModule {
    @Binds
    @Singleton
    abstract fun bindTmapRepository(
        impl: TmapRepositoryImpl
    ): TmapRepository

    @Binds
    abstract fun bindTmapDataSource(
        impl: TmapDataSourceImpl
    ): TmapDataSource
}