package com.storyapp.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "story")
data class Story(
    @PrimaryKey
    val id: String,
    val photoUrl: String,
    val name: String,
    val description: String,
    val createdAt: String,
)