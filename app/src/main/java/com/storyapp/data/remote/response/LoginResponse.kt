package com.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class LoginResponse(

	@field:SerializedName("loginResult")
	val loginResult: LoginResultResponse,

	@field:SerializedName("message")
	val message: String
)

