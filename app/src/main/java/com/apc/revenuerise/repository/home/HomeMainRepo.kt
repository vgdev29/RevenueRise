package com.apc.revenuerise.repository.home

import android.content.ContentResolver
import com.apc.revenuerise.dataClasses.CallLogEntry
import com.apc.revenuerise.dataClasses.Consumer
import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import com.apc.solarsuvidha.util.Resource


interface HomeMainRepo {
    suspend fun getAssignedConsumers(uid: Int): Resource<List<Consumer>>

    suspend fun getCallLogs(contentResolver: ContentResolver,
                            startDate: Long,
                            endDate: Long,
                            numbers: List<String>):
            Resource<List<CallLogEntry>>


}