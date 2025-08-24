package com.example.mocklocation.di

import com.example.mocklocation.data.datasource.CsvGpsParser
import com.example.mocklocation.domain.usecase.GpsDataParser
import com.example.mocklocation.domain.usecase.GpsParserAbstractFactory
import com.example.mocklocation.domain.usecase.GpsParserFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.InputStream
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ParserModule {
    @CsvParser
    @Provides
    @Singleton
    fun provideCsvParser(): GpsDataParser<InputStream> = CsvGpsParser()

    @Provides
    @Singleton
    fun provideGpsParserFactory(
        @CsvParser csvParser: GpsDataParser<InputStream>
    ): GpsParserAbstractFactory {
        return GpsParserFactory(csvParser)
    }
}