package com.udacity.project.spire.data.remote.api

import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton object that provides Retrofit instance and API service. In a more advanced implementation,
 * we can use a DI framework such as Dagger2 to inject dependencies.
 * Configures HTTP client with logging and timeouts.
 */
object ApiServiceProvider {

    /**
     * Public AWS Lambda Function URL hosting the Spire API (us-east-1).
     */
    private const val LAMBDA_URL = "https://nmzmnl3hly5odkriglauqar4uy0hinjr.lambda-url.us-east-1.on.aws/"

    /**
     * Base URL for the API (Lambda Function URL + the "api/" router prefix).
     */
    private const val BASE_URL = LAMBDA_URL + "api/"
     /* HTTP logging interceptor for debugging network requests.
     */
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * OkHttp client with logging and timeout configuration.
     */
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Gson instance with lenient parsing.
     */
    private val gson = GsonBuilder()
        .setStrictness(Strictness.LENIENT)
        .create()

    /**
     * Retrofit instance configured with base URL and converters.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Lazy-initialized API service instance.
     */
    val apiService: BuildingApiService by lazy {
        retrofit.create(BuildingApiService::class.java)
    }
}