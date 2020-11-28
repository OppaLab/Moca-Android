package com.oppalab.moca.dto

data class UpdateProfileDTO (
    val nickname: String,
    val userCategories: List<String>,
    val subscribeToPushNotification: Boolean
)