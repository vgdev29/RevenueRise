package com.apc.revenuerise.dataClasses

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Consumer(
    val ACCT_ID: String,
    val ADDRESS: String,
    val BILL_DATE: Long,
    val BUCKETING_PREDICTED: String,
    val DIV_CODE: String,
    val DT: String,
    val FEEDER: String,
    val LATITUDE: Double,
    val LOAD: Int,
    val LOAD_UNIT: String,
    val LONGTIUDE: Double,
    val MOBILE_NO: String,
    val MONTH: String,
    val NAME: String,
    val PREDICTED_ACTION: String,
    val PREDICTED_BEHAVIOR: String,
    val PRED_BILL: Double,
    val SDO_CODE: String,
    val SUBSTATION: String,
    val SUPPLY_TYPE: String,
    val USER: String,
    val YEAR: Int,
    val id: Int,
    val index: Int,
    var callingStatus:Int = 0,
    var callDuration:Int = 0,
    var callDate:Long = 0L,
    var callDetails: List<ServerCallLogsResItem>?=null

): Parcelable