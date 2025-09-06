package com.example.mocklocation.di

import com.example.mocklocation.data.parser.CsvGpsParser
import com.example.mocklocation.data.parser.TileSectionParser
import com.example.mocklocation.data.parser.TvasToWgs84Converter
import com.example.mocklocation.data.parser.VertexSectionParser
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

    @TvasKotlinParser
    @Provides
    @Singleton
    fun provideTvasParser(
        tileParser: TileSectionParser,
        vertexParser: VertexSectionParser,
        converter: TvasToWgs84Converter
    ): GpsDataParser<String> = com.example.mocklocation.data.parser.TvasKotlinParser(
        tileParser,
        vertexParser,
        converter
    )

    @TvasNativeParser
    @Provides
    @Singleton
    fun provideTvasNativeParser(): GpsDataParser<String> = com.example.mocklocation.data.parser.TvasNativeParser()

    @Provides
    fun provideTileParser(): TileSectionParser = TileSectionParser

    @Provides
    fun provideVertexParser(): VertexSectionParser = VertexSectionParser

    @Provides
    fun provideTvasConverter(): TvasToWgs84Converter = TvasToWgs84Converter()

    @Provides
    @Singleton
    fun provideGpsParserFactory(
        @CsvParser csvParser: GpsDataParser<InputStream>,
        @TvasKotlinParser tvasKotlinParser: GpsDataParser<String>,
        @TvasNativeParser tvasNativeParser:  GpsDataParser<String>
    ): GpsParserAbstractFactory {
        return GpsParserFactory(csvParser, tvasKotlinParser, tvasNativeParser)
    }
}