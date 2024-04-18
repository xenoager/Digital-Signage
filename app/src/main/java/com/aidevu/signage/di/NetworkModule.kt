package com.aidevu.signage.di

import com.aidevu.signage.Apps
import com.aidevu.signage.service.AddCookiesInterceptor
import com.aidevu.signage.service.ApiService
import com.aidevu.signage.service.ReceivedCookiesInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

// 서버 추후 연동
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    private val CONNECTION_TIMEOUT = 7L
    private val READ_TIMEOUT = 15L
    private val WRITE_TIMEOUT = 15L
    private val BASE_URL = "https://google.com"

    private var connectionPool_: ConnectionPool? = null
    private lateinit var client: OkHttpClient
    private lateinit var instance: NetworkModule
    private lateinit var service: ApiService
    private lateinit var retrofit: Retrofit

    @Provides
    @Singleton
    fun providerApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun retrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun providerOkHttpClient(): OkHttpClient {
        if (null == connectionPool_) {
            connectionPool_ = ConnectionPool()
        }
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        val in1 = AddCookiesInterceptor(Apps.getApplicationContext())
        val in2 = ReceivedCookiesInterceptor(Apps.getApplicationContext())
        client = OkHttpClient.Builder().connectionPool(connectionPool_!!)
                .addInterceptor(interceptor)
                .addInterceptor(in1)
                .addInterceptor(in2)
                .retryOnConnectionFailure(false)
                .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
        return client
    }
}