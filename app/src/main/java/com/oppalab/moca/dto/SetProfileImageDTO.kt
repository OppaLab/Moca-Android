package com.oppalab.moca.dto

import okhttp3.MultipartBody
import retrofit2.http.Multipart

data class SetProfileImageDTO(val userId: Long,
                              val profileImageFile: MultipartBody.Part)