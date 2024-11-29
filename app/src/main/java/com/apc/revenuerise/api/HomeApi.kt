package com.apc.revenuerise.api

import com.apc.revenuerise.dataClasses.Consumer
import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import com.apc.revenuerise.dataClasses.LoginRequest
import com.apc.revenuerise.dataClasses.LoginResponse
import com.apc.revenuerise.dataClasses.PostCallRecordRes
import com.apc.revenuerise.dataClasses.ServerCallLogsRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface HomeApi {
    @GET("/mob_call_sendList")
    suspend fun getConsumersForCalling(
        @Query("username") userId: String,
    ): Response<List<Consumer>>

    @GET("/receiveCallList/{mob}/{duration}/{date}")
    suspend fun postCallRecord(
        @Path("mob")  mob:String,
        @Path("duration")  duration:String,
        @Path("date")  date:String
    ):Response<PostCallRecordRes>

    @GET("/mob_call_sendList2")
    suspend fun getCallRecords(

    ):Response<ServerCallLogsRes>

    @POST("/userLogin/")
    @Headers("Content-Type: application/json")
    suspend fun loginUser(
        @Body loginReq : LoginRequest
    ): Response<LoginResponse>
}