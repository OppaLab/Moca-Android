package com.oppalab.moca.dto

import okhttp3.MultipartBody

data class CreatePostDTO (
    val thumbnailImageFile:MultipartBody.Part,
    val thumbnailImageFilePathName: String,
    val postTitle: String,
    val postBody: String,
    val userId: Long,
    val postCategories: List<String>
)