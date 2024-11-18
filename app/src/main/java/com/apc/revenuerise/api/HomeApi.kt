package com.apc.revenuerise.api

import com.apc.revenuerise.dataClasses.Consumer
import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface HomeApi {
    @GET("/mob_call_sendList")
    suspend fun getConsumersForCalling(
        @Query("user_id") userId: Int,
    ): Response<List<Consumer>>

}