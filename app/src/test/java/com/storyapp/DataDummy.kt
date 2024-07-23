package com.storyapp

import com.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStories(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "https://story-api.dicoding.dev/images/stories/photos-1687835748082_eBPHUFTZ.jpg",
                "Story $i",
                "Story Description $i",
                "2023-06-27T03:15:48.084Z",
                null,
                null
            )
            items.add(story)
        }
        return items
    }
}