package com.oppalab.moca.dto

data class GetPostDTO (
    val page: Int,
    val content: List<MyPostDTO>
)

