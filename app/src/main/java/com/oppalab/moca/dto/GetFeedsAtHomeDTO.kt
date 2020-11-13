package com.oppalab.moca.dto

import android.provider.ContactsContract

data class GetFeedsAtHomeDTO(
    val likeCount: Long,
    val commentCount: Long,
    val like: Boolean,
    val postId: Long,
    val userId: Long,
    val nickname: String,
    val profileImageFilePath: String,
    val postTitle: String,
    val postBody: String,
    val thumbnailImageFilePath: String,
    val createdAt: Long,
    val categories: List<String>
)