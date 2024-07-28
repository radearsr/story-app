package com.storyapp.data.remote.response

import com.google.gson.annotations.SerializedName

data class StoryItem(

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("photoUrl")
    val photoUrl: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("lon")
    val lon: Double? = null,

    @field:SerializedName("lat")
    val lat: Double? = null
)