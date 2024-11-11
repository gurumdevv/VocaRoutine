package com.gurumlab.vocaroutine.di

import com.gurumlab.vocaroutine.BuildConfig
import com.gurumlab.vocaroutine.data.source.remote.ApiCallAdapterFactory
import com.gurumlab.vocaroutine.data.source.remote.GptApiClient
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GptModule{

    @Singleton
    @GptOkHttpClient
    @Provides
    fun provideOkHttpClient(): OkHttpClient{
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val header = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer ${BuildConfig.GPT_API_KEY}")
                .build()
            chain.proceed(newRequest)
        }

        return OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(logger)
            .addInterceptor(header)
            .build()
    }

    @Singleton
    @GptRetrofit
    @Provides
    fun provideRetrofit(@GptOkHttpClient client: OkHttpClient, moshi: Moshi): Retrofit{
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideGptApiClient(@GptRetrofit retrofit: Retrofit): GptApiClient {
        return retrofit.create(GptApiClient::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GptRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GptOkHttpClient

object GptVersion{
    const val CURRENT_VERSION = "gpt-4o-mini"
}