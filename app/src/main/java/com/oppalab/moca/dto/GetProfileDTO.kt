package com.oppalab.moca.dto

data class GetProfileDTO(
    var nickname: String,
    var profileImageFilePath: String,
    var numberOfPosts: Long,
    var numberOfFollowers: Long,
    var numberOfFollowings: Long,
    var isFollowed: Boolean
)