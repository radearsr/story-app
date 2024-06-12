package com.storyapp.data.remote.response

data class DetailStoryResponse(
	val error: Boolean,
	val message: String,
	val story: ListStoryItem
)

