package com.example.mocklocation.di

import com.example.mocklocation.data.datasource.FileDataSource
import com.example.mocklocation.data.datasource.FileDataSourceImpl
import com.example.mocklocation.data.repository.FileRepositoryImpl
import com.example.mocklocation.domain.repository.FileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FileModule {
    @Binds
    @Singleton
    abstract fun bindFileDataSource(
        fileDataSourceImpl: FileDataSourceImpl
    ): FileDataSource

    @Binds
    @Singleton
    abstract fun bindFileRepository(
        fileRepositoryImpl: FileRepositoryImpl
    ): FileRepository
}
