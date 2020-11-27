package com.example.memo.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memo.databinding.RecycleItemTagLiteBinding
import com.example.memo.model.bean.TagLite

/**
Created by chene on @date 20-11-26 下午7:51
 **/
class TagLiteRecyclerViewAdapter(private val click: TagLiteClick) :
    ListAdapter<TagLite, RecyclerView.ViewHolder>(TagLiteDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TagLiteViewHolder(
            RecycleItemTagLiteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TagLiteViewHolder).bind(getItem(position), click)
    }

    class TagLiteViewHolder(
        private val binding: RecycleItemTagLiteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tag: TagLite, click: TagLiteClick) {
            binding.tag = tag
            binding.root.setOnClickListener {
                click.onClick(tag)
            }
            binding.executePendingBindings()
        }
    }
}

interface TagLiteClick {
    fun onClick(tag: TagLite)
}

private class TagLiteDiffCallBack : DiffUtil.ItemCallback<TagLite>() {
    override fun areItemsTheSame(oldItem: TagLite, newItem: TagLite): Boolean {
        return oldItem.tag == newItem.tag
    }

    override fun areContentsTheSame(oldItem: TagLite, newItem: TagLite): Boolean {
        return oldItem.tag == newItem.tag
    }
}