package com.oppalab.moca.dto

import android.provider.ContactsContract

data class GetFeedsAtHomeDTO(
    val page: Int,
    val content: List<FeedsAtHome>,
    val last: Boolean
)