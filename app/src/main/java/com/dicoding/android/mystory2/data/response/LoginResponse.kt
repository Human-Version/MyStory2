package com.dicoding.android.mystory2.data.response

data class LoginResponse(
    val error: Boolean,
    val loginResult: LoginResult,
    val message: String
)

data class LoginResult(
    var name: String,
    var token: String,
    var userId: String
)