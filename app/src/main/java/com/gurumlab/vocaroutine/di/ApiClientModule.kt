package com.gurumlab.vocaroutine.di

import com.gurumlab.vocaroutine.data.source.remote.ApiCallAdapterFactory
import com.gurumlab.vocaroutine.data.source.remote.ApiClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Singleton
    @FirebaseOkhttpClient
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val logger = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logger)
            .build()
    }

    @Singleton
    @FirebaseRetrofit
    @Provides
    fun provideRetrofit(@FirebaseOkhttpClient client: OkHttpClient, moshi: Moshi): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://vocaroutine-default-rtdb.asia-southeast1.firebasedatabase.app")
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .addCallAdapterFactory(ApiCallAdapterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideApiClient(@FirebaseRetrofit retrofit: Retrofit): ApiClient {
        return retrofit.create(ApiClient::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class FirebaseOkhttpClient