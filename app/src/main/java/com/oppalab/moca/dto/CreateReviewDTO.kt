package com.oppalab.moca.dto

data class CreateReviewDTO (
    val postId : Long,
    val userId : Long,
    val review : String
)