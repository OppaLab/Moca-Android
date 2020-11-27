package com.oppalab.moca.dto

data class CreateLikeDTO (
    val postId : Long,
    val reviewId : Long,
    val userId: Long
)