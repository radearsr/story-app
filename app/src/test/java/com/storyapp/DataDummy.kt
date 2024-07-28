package com.storyapp

import com.storyapp.data.local.model.Story

object DataDummy {
    fun generateDummyStories(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..100) {
            val story = Story(
                i.toString(),
                "https://story-api.dicoding.dev/images/stories/photos-1687835748082_eBPHUFTZ.jpg",
                "Story $i",
                "Story Description $i",
                "2023-06-27T03:15:48.084Z"
            )
            items.add(story)
        }
        return items
    }
}