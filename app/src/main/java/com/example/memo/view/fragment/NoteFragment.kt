package com.example.memo.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memo.databinding.FragmentNoteBinding
import com.example.memo.view.adapter.NoteRecyclerViewAdapter
import com.example.memo.viewmodel.MainActivityViewModel
import com.example.memo.viewmodel.NoteViwModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
Created by chene on @date 20-11-19 下午2:49
 resume rv not show the first
 **/
class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding
    private val noteViewModel: NoteViwModel by sharedViewModel()
    private val mainViewModel: MainActivityViewModel by sharedViewModel()
    private val adapter : NoteRecyclerViewAdapter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        context ?: return binding.root

        initView()
        subscribe()

        return binding.root
    }

    private fun initView(){
        binding.fragmentNoteRv.adapter = adapter
    }

    private fun subscribe(){
        mainViewModel.title.observe(viewLifecycleOwner){
            showNoteByTag(it)
        }
    }

    private fun showNoteByTag(tag: String) {
        (if (tag == "全部笔记") noteViewModel.getNotes() else noteViewModel.getNotesByTag(tag)).observe(viewLifecycleOwner){
            Log.d("TAG_18", "subscribe s")
            if (it.isEmpty()) {
                binding.fragmentNoteIvSearch.visibility = View.INVISIBLE
                binding.fragmentNoteEtSearch.visibility = View.INVISIBLE
                binding.fragmentNoteIvNoteNull.visibility = View.VISIBLE
                binding.fragmentNoteTvNoteNull.visibility = View.VISIBLE
                binding.fragmentNoteRv.visibility = View.INVISIBLE
            } else {
                binding.fragmentNoteIvSearch.visibility = View.VISIBLE
                binding.fragmentNoteEtSearch.visibility = View.VISIBLE
                binding.fragmentNoteIvNoteNull.visibility = View.INVISIBLE
                binding.fragmentNoteTvNoteNull.visibility = View.INVISIBLE
                binding.fragmentNoteRv.visibility = View.VISIBLE
            }
            adapter.submitList(it)
        }
    }
}