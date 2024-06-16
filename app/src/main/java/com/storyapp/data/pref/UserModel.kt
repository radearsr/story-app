package com.storyapp.data.pref

data class UserModel(
    val userId: String,
    val name: String,
    val token: String,
    var isLogin: Boolean = false
)
