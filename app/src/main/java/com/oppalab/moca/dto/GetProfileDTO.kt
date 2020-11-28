package com.oppalab.moca.dto

data class GetProfileDTO(
    val nickname: String,
    val profileImageFilePath: String,
    val numberOfPosts: Long,
    val numberOfFollowers: Long,
    val numberOfFollowings: Long,
    val IsFollowed: Boolean,
    val subscribeToPushNotification: Boolean,
    val userCategories: List<String>,
    val userEntities: List<String>
)