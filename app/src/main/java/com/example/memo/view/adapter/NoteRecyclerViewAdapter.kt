package com.example.memo.view.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.memo.databinding.RecycleItemNoteBinding
import com.example.memo.model.bean.Note
import com.example.memo.model.bean.Tag
import com.example.memo.view.activity.NoteEditActivity
import com.example.memo.viewmodel.MainActivityViewModel

/**
Created by chene on @date 20-11-20 下午3:31
 **/
class NoteRecyclerViewAdapter(
    private val longClick: View.OnLongClickListener,
    var mainViewModel: MainActivityViewModel
) : ListAdapter<Note, RecyclerView.ViewHolder>(NoteDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NoteViewHolder(
            RecycleItemNoteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as NoteViewHolder).bind(getItem(position), longClick, mainViewModel)
    }

    class NoteViewHolder(
        private val binding: RecycleItemNoteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            note: Note,
            longClick: View.OnLongClickListener,
            mainViewModel: MainActivityViewModel
        ) {
            binding.note = note
            binding.viewmodel = mainViewModel
            binding.setNavClick {
                val intent = Intent(it.context, NoteEditActivity::class.java).apply {
                    putExtra("createTime", note.createTime)
                }
                it.context.startActivity(intent)
            }
            binding.setDeleteClick {
                binding.recycleItemNoteDeleteBox.apply {
                    val before = isChecked
                    isChecked = !before
                }
            }
            binding.recycleItemNoteDeleteBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) mainViewModel.select(note.createTime) else mainViewModel.unSelect(
                    note.createTime
                )
            }
            binding.root.setOnLongClickListener {
                longClick.onLongClick(it)
                binding.recycleItemNoteDeleteBox.isChecked = true
                true
            }
            binding.root.setBackgroundColor(getNoteBackgroundColor(note.tag))
            binding.executePendingBindings()
        }

        private fun getNoteBackgroundColor(tag: String) = when (tag) {
            Tag.TAG_E_BOOK -> {
                Color.parseColor("#FFF3E0")
            }
            Tag.TAG_TRAVEL -> {
                Color.parseColor("#FFFDE7")
            }
            Tag.TAG_PERSONAL -> {
                Color.parseColor("#E3F2FD")
            }
            Tag.TAG_LIFE -> {
                Color.parseColor("#E8F5E9")
            }
            Tag.TAG_WORK -> {
                Color.parseColor("#ffebee")
            }
            else -> Color.WHITE
        }
    }

    private class NoteDiffCallBack : DiffUtil.ItemCallback<Note>() {
        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.createTime == newItem.createTime
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.content == newItem.content
        }
    }
}