package com.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoryResponse(

    @field:SerializedName("listStory")
	val listStory: List<StoryItem> = emptyList(),

    @field:SerializedName("message")
	val message: String
)