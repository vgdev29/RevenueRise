package com.apc.revenuerise.repository.home

import android.content.ContentResolver
import android.database.Cursor
import android.provider.CallLog
import android.util.Log
import com.apc.revenuerise.api.HomeApi
import com.apc.revenuerise.dataClasses.CallLogEntry
import com.apc.revenuerise.dataClasses.GetConsumersForCallingRes
import com.apc.revenuerise.dispatchers.DispatcherTypes
import com.apc.solarsuvidha.util.Resource
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeDefRepo @Inject constructor(
    private val apiService: HomeApi,
    private val dispatcherProvider: DispatcherTypes
) : HomeMainRepo {

    override suspend fun getAssignedConsumers(uid: Int)
    : Resource<GetConsumersForCallingRes> = withContext(dispatcherProvider.io) {
        try {
            // Attempt to login with the remote server
            val response = apiService.getConsumersForCalling(uid)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Log.d("RETRO>>", response.code().toString())
                Resource.Success(result)
            } else {

                // On failure, fallback to local database
                Log.d("RETRO>>", "Error")

             //   val res = consumerDao.getUser(loginReq.username)
                Resource.Error(response.message())

            }
        } catch (e: IOException) {
            // Network issue, use local database
          //  val res = userDao.getUser(loginReq.username)
            Resource.Error(e.message)

        }
    }

    override suspend fun getCallLogs(contentResolver: ContentResolver,
                                     startDate: Long,
                                     endDate: Long,
                                     numbers: List<String>)
        : Resource<List<CallLogEntry>> = withContext(dispatcherProvider.io) {
            try {
                val callLogEntries = mutableListOf<CallLogEntry>()

                // Prepare placeholders for the numbers list, e.g., (?, ?, ?)
                val numberPlaceholders = numbers.joinToString(",") { "?" }
                val selection = "${CallLog.Calls.TYPE} = ? AND ${CallLog.Calls.DATE} BETWEEN ? AND ? AND ${CallLog.Calls.NUMBER} IN ($numberPlaceholders)"

                // Combine the type, date range, and numbers into selectionArgs
                val selectionArgs = mutableListOf<String>().apply {
                    add(CallLog.Calls.OUTGOING_TYPE.toString())
                    add(startDate.toString())
                    add(endDate.toString())
                    addAll(numbers)
                }.toTypedArray()

                val cursor: Cursor? = contentResolver.query(
                    CallLog.Calls.CONTENT_URI,
                    null,
                    selection,
                    selectionArgs,
                    CallLog.Calls.DATE + " DESC"
                )

                cursor?.use {
                    val numberIndex = it.getColumnIndex(CallLog.Calls.NUMBER)
                    val typeIndex = it.getColumnIndex(CallLog.Calls.TYPE)
                    val dateIndex = it.getColumnIndex(CallLog.Calls.DATE)
                    val durationIndex = it.getColumnIndex(CallLog.Calls.DURATION)

                    while (it.moveToNext()) {
                        val number = it.getString(numberIndex)
                        val type = it.getInt(typeIndex)
                        val date = it.getLong(dateIndex)
                        val duration = it.getLong(durationIndex)

                        callLogEntries.add(CallLogEntry(number, type, date, duration))
                    }
                }

                Resource.Success(callLogEntries)
            } catch (e: IOException) {
                // Network issue, use local database
                //  val res = userDao.getUser(loginReq.username)
                Resource.Error(e.message)

            }
        }


}
