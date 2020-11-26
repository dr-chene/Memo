package com.example.memo.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memo.databinding.RecycleItemTagBinding
import com.example.memo.model.bean.Tag
import com.example.memo.viewmodel.MainActivityViewModel

/**
Created by chene on @date 20-11-22 下午1:35
 **/
class TagRecyclerViewAdapter(private val viewModel: MainActivityViewModel): ListAdapter<Tag, RecyclerView.ViewHolder>(TagDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TagViewHolder(
            RecycleItemTagBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TagViewHolder).bind(getItem(position), viewModel)
    }

    class TagViewHolder(
        private val binding: RecycleItemTagBinding
    ): RecyclerView.ViewHolder(binding.root){

        fun bind(tag: Tag, viewModel: MainActivityViewModel){
            binding.tag = tag
            binding.root.setOnClickListener {
                viewModel.selectTag(tag.tag)
                viewModel.sumNum(tag.count)
                Log.d("TAG_07", "bind: ${tag.tag}")
            }
            binding.executePendingBindings()
        }
    }

    private class TagDiffCallBack: DiffUtil.ItemCallback<Tag>(){
        override fun areItemsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem.tag == newItem.tag
        }

        override fun areContentsTheSame(oldItem: Tag, newItem: Tag): Boolean {
            return oldItem.count == newItem.count
        }
    }
}