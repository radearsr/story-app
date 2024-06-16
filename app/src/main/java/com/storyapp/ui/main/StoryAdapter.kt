package com.storyapp.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.storyapp.data.remote.response.ListStoryItem
import com.storyapp.databinding.ItemStoryListBinding
import com.storyapp.utils.getTimeAgo

class StoryAdapter(private val listStory: List<ListStoryItem>) :
    RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {
    class ListViewHolder(var binding: ItemStoryListBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemStoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val (id, photoUrl, name, _, createdAt) = listStory[position]
        with(holder.binding) {
            tvItemName.text = name
            Glide.with(root).load(photoUrl).into(ivItemPhoto)
            val timeAgo = getTimeAgo(holder.itemView.context, createdAt)
            tvItemDate.text = timeAgo
        }
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(id)
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(storyId: String)
    }
}