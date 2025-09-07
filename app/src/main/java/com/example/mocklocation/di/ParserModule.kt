package com.example.mocklocation.di

import com.example.mocklocation.data.parser.CsvGpsParser
import com.example.mocklocation.data.parser.TileSectionParser
import com.example.mocklocation.data.parser.TvasToWgs84Converter
import com.example.mocklocation.data.parser.VertexSectionParser
import com.example.mocklocation.domain.parser.GpsDataParser
import com.example.mocklocation.domain.factory.GpsParserAbstractFactory
import com.example.mocklocation.data.factory.GpsParserFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import java.io.InputStream
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ParserModule {
    @CsvParser
    @Provides
    @Singleton
    fun provideCsvParser(
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): GpsDataParser<InputStream> = CsvGpsParser(ioDispatcher = ioDispatcher)

    @TvasKotlinParser
    @Provides
    @Singleton
    fun provideTvasParser(
        tileParser: TileSectionParser,
        vertexParser: VertexSectionParser,
        converter: TvasToWgs84Converter,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): GpsDataParser<String> = com.example.mocklocation.data.parser.TvasKotlinParser(
        tileParser,
        vertexParser,
        converter,
        ioDispatcher
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
    fun provideTvasConverter(
        @DefaultDispatcher defaultDispatcher: CoroutineDispatcher
    ): TvasToWgs84Converter = TvasToWgs84Converter(defaultDispatcher)

    @Provides
    @Singleton
    fun provideGpsParserFactory(
        @CsvParser csvParser: GpsDataParser<InputStream>,
        @TvasKotlinParser tvasKotlinParser: GpsDataParser<String>,
        @TvasNativeParser tvasNativeParser: GpsDataParser<String>
    ): GpsParserAbstractFactory {
        return GpsParserFactory(csvParser, tvasKotlinParser, tvasNativeParser)
    }
}