package com.oppalab.moca.dto

data class CommentsOnPost (
    val page: Int,
    val content: List<CommentsOnPost>,
    val last: Boolean
)