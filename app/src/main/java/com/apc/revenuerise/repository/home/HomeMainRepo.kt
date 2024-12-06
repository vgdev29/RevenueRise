package com.apc.revenuerise.repository.home

import android.content.ContentResolver
import android.util.Log
import com.apc.lossreduction.dataClasses.geocode.GeocodeResponse
import com.apc.revenuerise.dataClasses.CallLogEntry
import com.apc.revenuerise.dataClasses.Consumer
import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import com.apc.revenuerise.dataClasses.LoginRequest
import com.apc.revenuerise.dataClasses.LoginResponse
import com.apc.revenuerise.dataClasses.PostCallRecordRes
import com.apc.revenuerise.dataClasses.ServerCallLogsRes
import com.apc.revenuerise.vms.HomeViewModel
import com.apc.solarsuvidha.util.Resource
import kotlinx.coroutines.withContext
import java.io.IOException


interface HomeMainRepo {


    suspend fun getAssignedConsumers(uid: String): Resource<List<Consumer>>

    suspend fun getCallLogs(contentResolver: ContentResolver,
                            startDate: Long,
                            endDate: Long,
                            numbers: List<String>):
            Resource<List<CallLogEntry>>

    suspend fun getLastCallDetails(contentResolver: ContentResolver):
        Resource<CallLogEntry>

    suspend fun postCallRecord(mob:String,duration:String,date:String):
            Resource<PostCallRecordRes>

    suspend fun getServerCallRecord():
            Resource<ServerCallLogsRes>

    suspend fun loginUser(loginRequest: LoginRequest): Resource<LoginResponse>

    suspend fun geocode(key: String, address: String): Resource<GeocodeResponse>


}