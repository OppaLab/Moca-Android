package com.oppalab.moca.dto

data class GetReviewDTO (
    val likeCount: Long,
    val commentCount: Long,
    val like: Boolean,
    val userId: Long,
    val nickname: String,
    val review: String,
    val profileImageFilePath: String,
    val createdAt: Long,
)