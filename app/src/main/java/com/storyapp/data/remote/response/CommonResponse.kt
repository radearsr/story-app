package com.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class CommonResponse(

	@field:SerializedName("error")
	val error: Boolean,

	@field:SerializedName("message")
	val message: String
)
