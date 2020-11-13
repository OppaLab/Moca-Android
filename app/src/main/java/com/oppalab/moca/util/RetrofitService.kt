package com.oppalab.moca.util

import com.oppalab.moca.dto.CreatePostDTO
import com.oppalab.moca.dto.GetFeedsAtHomeDTO
import com.oppalab.moca.dto.GetMyPostDTO
import com.oppalab.moca.dto.SignUpDTO
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {
    //user
    @POST("/signup")
    fun signUp(
        @Field("nickname") nickname: String,
        @Field("email") email: String,
        @Field("userCategoryList") userCategoryList: List<String>
    ): Call<SignUpDTO>

    //post
    @GET("/feed")
    fun getFeedsAtHome(
        @Query("userId") userId: Long,
        @Query("page") page: Long
    ): Call<GetFeedsAtHomeDTO>

    @GET("/post")
    fun getMyPosts(
        @Query("userId") userId: Long,
        @Query("page") page: Long
    ): Call<GetMyPostDTO>

    @Multipart
    @POST("/post")
    fun createPost(
        @Query("thumbnailImageFilePathName") thumbnailImageFilePathName:String,
        @Query("postTitle") postTitle:String,
        @Query("postBody") postBody:String,
        @Query("userId") userId:Long,
        @Query("postCategories") postCategories:List<String>,
        @Part thumbnailImageFile: MultipartBody.Part
    ): Call<CreatePostDTO>

    @DELETE("/post")
    fun deletePost(
        @Query("postId") postId: Long,
        @Query("userId") userId: Long
    )
}