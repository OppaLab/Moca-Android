data class GetCommentsOnPostDTO (
    val userId : Long,
    val nickname : String,
    val comment : String,
    val createdAt : Long,
    val page : Long
)