package com.apc.revenuerise.dataClasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerCallLogsResItem(
    val CALL_DATE_TIME: Long,
    val CALL_DURATION: Int,
    val MOBILE_NO: String,
    val id: Int,
    val index: Int
):Parcelable