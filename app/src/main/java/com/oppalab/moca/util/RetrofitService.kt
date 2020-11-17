package com.oppalab.moca.util

import GetCommentsOnPostDTO
import com.oppalab.moca.dto.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import okio.BufferedSource
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //user

    @FormUrlEncoded
    @POST("/signup")
    fun signUp(
        @Field("nickname") nickname: String,
        @Field("email") email: String,
        @Field("userCategoryList") userCategoryList: List<String>
    ): Call<Long>

    @Multipart
    @POST("/profileImage")
    fun setProfileImage(
        @Query("userId") userId: Long,
        @Part profileImageFile: MultipartBody.Part
    ): Call<String>

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
    @POST("/post")                  /******************SUCCEESS*********************/
    fun createPost(
        @Query("postTitle") postTitle:String,
        @Query("postBody") postBody:String,
        @Query("userId") userId:Long,
        @Query("postCategories") postCategories:List<String>,
        @Part thumbnailImageFile: MultipartBody.Part
    ): Call<Long>

    @DELETE("/post")
    fun deletePost(
        @Query("postId") postId: Long,
        @Query("userId") userId: Long
    ): Call<Long>

    @FormUrlEncoded
    @POST("/review")
    fun createReview(
        @Field ("postId") postId : Long,
        @Field ("userId") userId : Long,
        @Field ("review") review : String
    ): Call<Long>

    @GET("/comment")
    fun getCommentOnPost(
        @Query("postId") postId: Long,
        @Query("reviewId") reviewId: String,
        @Query("page") page: Long
    ): Call<GetCommentsOnPostDTO>

    //like

    @FormUrlEncoded
    @POST("/like")
    fun likePost(
        @Field("postId") postId: Long,
        @Field("userId") userId: Long,
        @Field("reviewId") reviewId: String,
    ): Call<Long>

    @FormUrlEncoded
    @DELETE("/unlike")
    fun unlikePost(
        @Query("postId") postId: Long,
        @Query("userId") userId: Long,
        @Query("reviewId") reviewId: String,
    ): Call<Long>

    //comment

    @FormUrlEncoded
    @POST("/comment")
    fun createComment(
        @Field("postId") postId: Long,
        @Field ("reviewId") reviewId: String,
        @Field("userId") userId: Long,
        @Field("comment") comment : String
    ): Call<Long>

    @FormUrlEncoded
    @DELETE("/comment")
    fun deleteComment(
        @Field("commentId") commentId: Long,
        @Field ("userId") reviewId: Long
    ): Call<Long>
}