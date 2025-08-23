package com.example.mocklocation.di

import com.example.mocklocation.data.parser.CsvGpsParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ParserModule {
    @Provides
    @Singleton
    fun provideCsvGpsParser(): CsvGpsParser {
        return CsvGpsParser()
    }
}