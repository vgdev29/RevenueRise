package com.apc.revenuerise.api

import com.apc.lossreduction.dataClasses.geocode.GeocodeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeoApi {
    @GET("/maps/api/geocode/json")
    suspend fun geocode(
        @Query("key") apiKey: String,
        @Query("address") address: String
    ): Response<GeocodeResponse>


}