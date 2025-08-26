package com.example.mocklocation.di

import com.example.mocklocation.data.service.TmapService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TmapServiceModule {
    @Provides
    @Singleton
    @Named("tmapService")
    fun provideTmapService(@TmapRetrofitType retrofit: Retrofit): TmapService {
        return retrofit.create(TmapService::class.java)
    }

    @Provides
    @Singleton
    @Named("autocompleteTmapService")
    fun provideTmapAutoCompleteService(@AutoCompleteRetrofitType autocompleteRetrofit: Retrofit): TmapService {
        return autocompleteRetrofit.create(TmapService::class.java)
    }
}