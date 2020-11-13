package com.oppalab.moca.dto

import okhttp3.MultipartBody

data class SetProfileImageDTO(
    val userId: Long,
    val profileImageFile: MultipartBody.Part
)