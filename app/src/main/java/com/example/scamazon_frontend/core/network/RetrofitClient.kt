package com.example.scamazon_frontend.core.network

import android.content.Context
import com.example.scamazon_frontend.core.utils.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // 10.0.2.2 is the special IP alias to your host loopback interface for Android Emulator
    private const val BASE_URL = "http://10.0.2.2:5255"

    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit {
        if (retrofit == null) {
            val tokenManager = TokenManager(context)

            // Logging Interceptor for debugging
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            // Auth Interceptor to add JWT token to requests
            val authInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val token = tokenManager.getToken()

                val requestBuilder = originalRequest.newBuilder()
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer \$token")
                }
                
                chain.proceed(requestBuilder.build())
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}
