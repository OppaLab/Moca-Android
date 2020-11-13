package com.oppalab.moca.`interface`

import com.oppalab.moca.dto.GetProfileImageDTO
import com.oppalab.moca.dto.GetThumbnailImageDTO
import com.oppalab.moca.dto.SetProfileImageDTO
import com.oppalab.moca.dto.SignUpDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    @POST("/signup")
    fun signup(
        @Field("nickname") nickname: String,
        @Field("userCategoryList") userCategoryList: List<String>,
        @Field("email") email: String
    ): Call<SignUpDTO>

    @Multipart
    @POST("/profileImage")
    fun setProfileImage(
        @Query("userId") userId: Long,
        @Part profileImageFile: MultipartBody.Part
    ): Call<SetProfileImageDTO>

    @GET("/image/profile/{fileName}")
    fun getProfileImage(
        @Query("fileName") filename: String
    ): Call<GetProfileImageDTO>

    @GET("/image/thumbnail/{fileName}")
    fun getThumbnailImage(
        @Query("fileName") filename: String
    ): Call<GetThumbnailImageDTO>

//        Call<ResponseBody> postFile(@Part MultipartBody.Part file, @Part("description") RequestBody description);
//
}