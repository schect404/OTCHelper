package com.example.data.retrofit

import com.example.data.BuildConfig
import com.example.data.network.NetworkContract
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitFactoryImpl(
    private val okHttpClient: OkHttpClient
) : RetrofitFactory {

    override fun createRetrofit(gson: Gson): Retrofit {

        val okHttpBuilder = okHttpClient.newBuilder()
            .apply {
                if (BuildConfig.DEBUG)
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
            }
            .connectTimeout(TIMEOUT_SECS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECS, TimeUnit.SECONDS)
            .build()

        val builder: Retrofit.Builder = Retrofit.Builder()
            .client(okHttpBuilder)
            .baseUrl("https://backend.otcmarkets.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))

        return builder.build()
    }

    companion object {
        private const val TIMEOUT_SECS: Long = 120
    }

}