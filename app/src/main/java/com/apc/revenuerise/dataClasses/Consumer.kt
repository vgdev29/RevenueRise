package com.apc.revenuerise.dataClasses

data class Consumer(
    val ACCT_ID: String,
    val ADDRESS: String,
    val Adjusted_Average_Bill: Double,
    val BUCKETING_PREDICTED: String,
    val FEEDER: String,
    val LOAD: Int,
    val LOAD_UNIT: String,
    val MOBILE_NO: String,
    val MONTH: String,
    val NAME: String,
    val PREDICTED_ACTION: String,
    val PREDICTED_BEHAVIOR: String,
    val SDO_CODE: String,
    val SUBSTATION: String,
    val SUPPLY_TYPE: String,
    val YEAR: Int,
    val id: Int
)