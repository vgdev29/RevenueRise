package com.apc.revenuerise.dataClasses

data class LoginResponse(
    val error: Boolean,
    val message: String,
    val success: Boolean,
    val userDetails: UserDetails
)