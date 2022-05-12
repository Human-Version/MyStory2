package com.dicoding.android.mystory2.data.response

data class UserSession(
    val name: String,
    val token: String,
    val userId: String,
    val isLogin: Boolean
)
