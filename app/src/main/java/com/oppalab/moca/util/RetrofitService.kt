package com.oppalab.moca.util

import GetCommentsOnPostDTO
import com.oppalab.moca.dto.*
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

    @Multipart
    @POST("/profileImage")
    fun setProfileImage(
        @Query("userId") userId: Long,
        @Part profileImageFile: MultipartBody.Part
    ): Call<SetProfileImageDTO>

    @GET("/image/profile/{fileName}")
    fun getProfileImage(
        @Path("fileName") filename: String
    ): Call<GetProfileImageDTO>


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

    @POST("/review")
    fun createReview(
        @Field ("postId") postId : Long,
        @Field ("userId") userId : Long,
        @Field ("review") review : String
    ): Call<CreateReviewDTO>

    @GET("/comment")
    fun getCommentOnPost(
        @Query("userId") userId: Long,
        @Query("nickname") nickname : String,
        @Query("comment") comment: String,
        @Query("createdAt") createdAt: Long,
        @Query("page") page: Long
    ): Call<GetCommentsOnPostDTO>

    @GET("/image/thumbnail/{fileName}")
    fun getThumbnailImage(
        @Path("fileName") filename: String
    ): Call<GetThumbnailImageDTO>

    //like

    @POST("/like")
    fun likePost(
        @Field("postId") postId: Long,
        @Field ("reviewId") reviewId: Long,
        @Field("userId") userId: Long,
    ): Call<CreateLikeDTO>

    @DELETE("/unlike")
    fun unlikePost(
        @Field("postId") postId: Long,
        @Field ("reviewId") reviewId: Long,
        @Field("userId") userId: Long,
    )

    //comment

    @POST("/comment")
    fun createComment(
        @Field("postId") postId: Long,
        @Field ("reviewId") reviewId: Long,
        @Field("userId") userId: Long,
        @Field("comment") comment : String
    ): Call<CreateCommentDTO>

    @DELETE("/comment")
    fun deleteComment(
        @Field("commentId") commentId: Long,
        @Field ("reviewId") reviewId: Long
    )
}