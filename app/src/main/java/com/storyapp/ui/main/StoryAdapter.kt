package com.storyapp.ui.main

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.storyapp.R
import com.storyapp.data.remote.response.StoryItemResponse
import com.storyapp.databinding.ItemStoryListBinding
import com.storyapp.ui.main.detail.DetailStoryActivity
import com.storyapp.utils.getTimeAgo

class StoryAdapter : PagingDataAdapter<StoryItemResponse,StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {
    class ListViewHolder(var binding: ItemStoryListBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemStoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val storyItem = getItem(position)
        storyItem?.let { story ->
            with(holder.binding) {
                tvItemName.text = story.name
                Glide
                    .with(root)
                    .load(story.photoUrl)
                    .placeholder(R.drawable.ic_place_holder)
                    .into(object: CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            Log.d("StoryAdapter", "OnResourceReady")
                            ivItemPhoto.setImageDrawable(resource)
                            ivItemPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {
                            Log.d("StoryAdapter", "onLoadCleared")
                            ivItemPhoto.setImageDrawable(placeholder)
                            ivItemPhoto.scaleType = ImageView.ScaleType.FIT_CENTER
                        }
                    })
                val timeAgo = getTimeAgo(holder.itemView.context, story.createdAt)
                tvItemDate.text = timeAgo
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(holder.itemView.context, DetailStoryActivity::class.java)
                intent.putExtra(DetailStoryActivity.EXTRA_STORY_ID, story.id)
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        holder.itemView.context as Activity,
                        Pair(holder.binding.ivItemPhoto, "photo"),
                        Pair(holder.binding.tvItemName, "name"),
                        Pair(holder.binding.tvItemDate, "createdAt")
                    )
                holder.itemView.context.startActivity(intent, optionsCompat.toBundle())
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryItemResponse>() {
            override fun areItemsTheSame(oldItem: StoryItemResponse, newItem: StoryItemResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StoryItemResponse,
                newItem: StoryItemResponse
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}