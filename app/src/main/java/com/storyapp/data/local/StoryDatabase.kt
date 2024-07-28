package com.storyapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.storyapp.data.local.model.RemoteKeys
import com.storyapp.data.local.model.Story

@Database(
    entities = [Story::class, RemoteKeys::class],
    version = 2,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}