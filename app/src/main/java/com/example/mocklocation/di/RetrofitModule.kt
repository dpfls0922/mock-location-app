package com.example.mocklocation.di

import com.example.mocklocation.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RetrofitModule {
    // Headers
    private const val CONTENT_TYPE = "Content-Type"
    private const val ACCEPT = "Accept"
    private const val REQUESTER = "Requester"
    private const val CLIENT_REQ_TIME = "Client_ReqTime"
    private const val NETWORK_TYPE = "Network-Type"
    private const val NONCE = "Nonce"
    private const val CIH = "CIH"

    // Header Values
    private const val APPLICATION_JSON = "application/json"
    private const val REQUESTER_CLIENT_SSL = "CLIENT_SSL"
    private const val NETWORK_WIFI = "WIFI"
    private const val DEFAULT_NONCE = "1499634157"
    private const val DEFAULT_CIH = "-86050318"

    @Provides
    @Singleton
    @Named("TmapAccessKey")
    fun provideAccessKey(): String {
        return BuildConfig.TMAP_ACCESS_KEY
    }

    @Provides
    @Singleton
    fun provideCustomHeadersInterceptor(
        @Named("TmapAccessKey") accessKey: String
    ): Interceptor {
        return Interceptor { chain ->
            val requestTime = SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(Date())

            val request = chain.request().newBuilder()
                .header(CONTENT_TYPE, APPLICATION_JSON)
                .header(ACCEPT, APPLICATION_JSON)
                .header(REQUESTER, REQUESTER_CLIENT_SSL)
                .header(CLIENT_REQ_TIME, requestTime)
                .header(NETWORK_TYPE, NETWORK_WIFI)
                .header(NONCE, DEFAULT_NONCE)
                .header(CIH, DEFAULT_CIH)
                .build()

            Timber.d("Request Headers: ${request.headers}")
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        customHeadersInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(customHeadersInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @TmapRetrofitType
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @AutoCompleteRetrofitType
    fun provideAutocompleteRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BuildConfig.AUTO_COMPLETE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}