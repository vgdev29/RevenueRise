package com.apc.revenuerise.api

import com.apc.revenuerise.dataClasses.Consumer
import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import com.apc.revenuerise.dataClasses.PostCallRecordRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {
    @GET("/mob_call_sendList")
    suspend fun getConsumersForCalling(
        @Query("user_id") userId: Int,
    ): Response<List<Consumer>>

    @GET("/receiveCallList/{mob}/{duration}/{date}")
    suspend fun postCallRecord(
        @Path("mob")  mob:String,
        @Path("duration")  duration:String,
        @Path("date")  date:String
    ):Response<PostCallRecordRes>

}