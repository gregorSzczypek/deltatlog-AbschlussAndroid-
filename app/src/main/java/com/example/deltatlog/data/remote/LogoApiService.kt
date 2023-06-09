package com.example.deltatlog.data.remote

import com.example.deltatlog.data.datamodels.Logo
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Base URL for the API
val BASE_URL = "https://company.clearbit.com/v1/domains/"

// Uncomment the line below and provide your API key
//API_KEY = "sk_1c15a6f5d0a52350c5e50ff9abcb24b1"

// Create an instance of Moshi JSON parser
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

// Create a Retrofit instance
private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

// Define the API service interface
interface LogoApiService {
    @GET("find?name=")
    suspend fun getLogo(@Query("name") name: String,
                        @Header("Authorization") authHeader: String
    ): Logo
}

// object to access the API service
object LogoApi {
    val retrofitService: LogoApiService by lazy { retrofit.create(LogoApiService::class.java) }
}