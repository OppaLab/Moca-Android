package com.oppalab.moca.dto

data class CreateCommentDTO (
    val postId: Long,
    val reviewId: Long,
    val userId: Long,
    val comment : String
)