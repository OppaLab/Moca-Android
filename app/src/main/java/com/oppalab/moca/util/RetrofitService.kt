package com.oppalab.moca.util

import GetCommentsOnPostDTO
import com.oppalab.moca.dto.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface RetrofitService {

    //user

    @FormUrlEncoded
    @POST("/signup")
    fun signUp(
        @Field("nickname") nickname: String,
        @Field("email") email: String,
        @Field("userCategoryList") userCategoryList: List<String>,
        @Field("registrationToken") registrationToken: String
    ): Call<Long>

    @FormUrlEncoded
    @POST("/signin")
    fun signIn(
        @Field("email") email: String
    ): Call<Long>

    @Multipart
    @POST("/profileImage")
    fun setProfileImage(
        @Query("userId") userId: Long,
        @Part profileImageFile: MultipartBody.Part
    ): Call<String>

    @GET("/profile")
    fun getProfile(
        @Query("myUserId") myUserId: Long,
        @Query("userId") userId: Long
    ): Call<GetProfileDTO>

    @FormUrlEncoded
    @PUT("/profile/{userId}")
    fun updateProfile(
        @Path("userId") userId: Long,
        @Field("nickname") nickname: String,
        @Field("userCategories") userCategories: List<String>,
        @Field("subscribeToPushNotification") subscribeToPushNotification: Boolean
    ): Call<Long>

    @DELETE("/signout/{userId}")
    fun signOut(
        @Path("userId") userId: Long
    ): Call<Long>


    //follow & follower

    @FormUrlEncoded
    @POST("/follow")
    fun followUser(
        @Field("userId") userId: Long,
        @Field("followedUserId") followedUserId: Long
    ): Call<Long>

    @DELETE("/unfollow")
    fun unfollowUser(
        @Query("userId") userId: Long,
        @Query("followedUserId") followedUserId: Long
    ): Call<Long>

    //post

    @GET("/feed")
    fun getFeedsAtHome(
        @Query("userId") userId: Long,
        @Query("page") page: Long
    ): Call<GetFeedsAtHomeDTO>

    @GET("/post")
    fun getPosts(
        @Query("userId") userId: Long,
        @Query("search") search: String,
        @Query("category") category: String,
        @Query("page") page: Long,
        @Query("sort") sort: String
    ): Call<GetMyPostDTO>

    @GET("/post")
    fun getOnePost(
        @Query("userId") userId: Long,
        @Query("postId") postId: Long,
        @Query("search") search: String,
        @Query("category") category: String,
        @Query("page") page: Long
    ): Call<GetMyPostDTO>

    @POST("/report")
    fun report(
        @Query("userId") userId: Long,
        @Query("reportedUserId") reportedUserId: Long,
        @Query("postId") postId: Long,
        @Query("reviewId") reviewId: Long,
        @Query("commentId") commentId: Long,
        @Query("reportReason") reportReason: String
    ): Call<Long>

    @Multipart
    @POST("/post")                  /******************SUCCEESS*********************/
    fun createPost(
        @Query("postTitle") postTitle:String,
        @Query("postBody") postBody:String,
        @Query("userId") userId:Long,
        @Query("postCategories") postCategories:List<String>,
        @Part thumbnailImageFile: MultipartBody.Part,
        @Query("numberOfRandomUserPushNotification") numberOfRandomUserPushNotification: Long,
        @Query("isRandomUserPushNotification") isRandomUserPushNotification: Boolean,

    ): Call<Long>

    @PUT("/post/{postId}")
    fun updatePost(
        @Path("postId") postId: String,
        @Query("postTitle") postTitle:String,
        @Query("postBody") postBody:String,
        @Query("userId") userId:Long,
        @Query("postCategories") postCategories:List<String>,
    ): Call<Long>

    @PUT("/review/{reviewId}")
    fun updateReview(
        @Path("reviewId") reviewId: String,
        @Query("review") review: String
    ): Call<Long>

    @DELETE("/review")
    fun deleteReview(
        @Query("reviewId") reviewId: Long,
        @Query("userId") userId: Long
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
        @Query("postId") postId: String,
        @Query("reviewId") reviewId: String,
        @Query("page") page: Long
    ): Call<GetCommentsOnPostDTO>

    //like

    @FormUrlEncoded
    @POST("/like")
    fun likePost(
        @Field("postId") postId: String,
        @Field("userId") userId: Long,
        @Field("reviewId") reviewId: String,
    ): Call<Long>

    @DELETE("/unlike")
    fun unlikePost(
        @Query("postId") postId: String,
        @Query("userId") userId: Long,
        @Query("reviewId") reviewId: String,
    ): Call<Long>

    //comment

    @FormUrlEncoded
    @POST("/comment")
    fun createComment(
        @Field("postId") postId: String,
        @Field ("reviewId") reviewId: String,
        @Field("userId") userId: Long,
        @Field("comment") comment : String
    ): Call<Long>

    @DELETE("/comment")
    fun deleteComment(
        @Query ("commentId") commentId: Long,
        @Query ("userId") userId: Long
    ): Call<Long>

    @GET("/review")
    fun getReview(
        @Query ("userId") userId: String,
        @Query ("reviewId") reviewId: String
    ): Call<GetReviewDTO>

    //notifications
    @GET("/activity")
    fun getNotifications(
        @Query("userId") userId: Long,
        @Query("page") page: Long
    ): Call<GetNotificationsDTO>



}