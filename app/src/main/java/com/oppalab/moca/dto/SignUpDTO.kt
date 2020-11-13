package com.oppalab.moca.dto

data class SignUpDTO (val nickname: String,
                      val email: String,
                      val userCategoryList: List<String>)