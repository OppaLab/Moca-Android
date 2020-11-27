import com.oppalab.moca.dto.CommentsOnPost
import com.oppalab.moca.dto.FeedsAtHome

data class GetCommentsOnPostDTO (
    val page: Int,
    val content: List<CommentsOnPost>,
)