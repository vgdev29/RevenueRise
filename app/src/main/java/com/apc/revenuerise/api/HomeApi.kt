package com.apc.revenuerise.api

import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("/api/get_consumers_for_rr.php")
    suspend fun getConsumersForCalling(
        @Query("user_id") userId: Int,
    ): Response<GetConsumersForCallingRes>

}