package com.apc.revenuerise.dataClasses

data class GetConsumersForCallingRes(
    val consumers: List<Consumer>,
    val error: Boolean,
    val message: String
)