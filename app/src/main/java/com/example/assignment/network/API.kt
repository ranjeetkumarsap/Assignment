package com.example.assignment.network


import com.example.assignment.model.*
import retrofit2.Response
import retrofit2.http.*


interface API {


    @GET("weather")
    fun getCurrentWeather(
        @Query("q") q: String?,
        @Query("units") units: String?,
        @Query("lang") lang: String?,
        @Query("appid") appId: String?
    ): Response<CurrentWeatherResponse?>

    @GET
    suspend fun getCurrentWeather(
        @Url url:String
    ): Response<CurrentWeatherResponse?>


}