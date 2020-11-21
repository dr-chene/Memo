package com.example.memo.view.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memo.databinding.RecycleItemNoteBinding
import com.example.memo.model.bean.Note
import com.example.memo.view.activity.NoteEditActivity

/**
Created by chene on @date 20-11-20 下午3:31
 **/
class NoteRecyclerViewAdapter : ListAdapter<Note, RecyclerView.ViewHolder>(NoteDiffCallBack()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NoteViewHolder(
            RecycleItemNoteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NoteViewHolder).bind(getItem(position))
    }

    class NoteViewHolder(
        private val binding: RecycleItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note){
            binding.note = note
            binding.setNavClick {
                val intent = Intent(it.context, NoteEditActivity::class.java).apply {
                    putExtra("createTime", note.createTime)
                }
                it.context.startActivity(intent)
            }
            binding.executePendingBindings()
        }
    }

    private class NoteDiffCallBack : DiffUtil.ItemCallback<Note>(){
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.createTime == newItem.createTime
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.content == newItem.content
        }
    }
}