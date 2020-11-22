package com.example.memo.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memo.databinding.FragmentNoteBinding
import com.example.memo.model.bean.Note
import com.example.memo.view.adapter.NoteRecyclerViewAdapter
import com.example.memo.viewmodel.MainActivityViewModel
import com.example.memo.viewmodel.NoteViwModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
Created by chene on @date 20-11-19 下午2:49
 **/
class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding
    private val noteViwModel: NoteViwModel by sharedViewModel()
    private val mainViewModel: MainActivityViewModel by sharedViewModel()
    private val adapter : NoteRecyclerViewAdapter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        context ?: return binding.root

        initView()
        subscribe()

        return binding.root
    }

    private fun initView(){

    }

    private fun subscribe(){
        mainViewModel.title.observe(viewLifecycleOwner){
            showNoteByTag(it)
        }
    }

    private fun showNoteByTag(tag: String) {
        val notes = if (tag == "全部笔记") noteViwModel.getNotes()
            else noteViwModel.getNotesByTag(tag)
        notes.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }
    }
}