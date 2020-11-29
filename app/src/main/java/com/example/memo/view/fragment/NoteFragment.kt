package com.example.memo.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.memo.R
import com.example.memo.databinding.FragmentNoteBinding
import com.example.memo.model.bean.Note
import com.example.memo.view.adapter.NoteRecyclerViewAdapter
import com.example.memo.viewmodel.MainActivityViewModel
import com.example.memo.viewmodel.NoteViwModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf

/**
Created by chene on @date 20-11-19 下午2:49
resume rv not show the first
 **/
class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding
    private val noteViewModel: NoteViwModel by sharedViewModel()
    private val mainViewModel: MainActivityViewModel by sharedViewModel()
    private val adapter: NoteRecyclerViewAdapter by inject { parametersOf(longClick) }
    private lateinit var list: List<Note>
    private var isFirst = true
    private var selectTag = "全部笔记"
    private val longClick by lazy {
        View.OnLongClickListener {
            mainViewModel.enterDeleteMode()
            true
        }
    }

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

    override fun onResume() {
        super.onResume()
        adapter.notifyItemChanged(0)
    }

    private fun initView() {
        binding.fragmentNoteRv.adapter = adapter
        binding.fragmentNoteEtSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                noteViewModel.enterSearchMode()
            }
        }
        binding.fragmentNoteIvBack.setOnClickListener {
            noteViewModel.exitSearchMode()
        }
        binding.fragmentNoteEtSearch.addTextChangedListener {
            if (it != null) {
                if (it.toString().isNotEmpty() && it.toString() != "") {
                    binding.fragmentNoteIvCancel.visibility = View.VISIBLE
                    noteViewModel.getSearchNote(it.toString()).observe(viewLifecycleOwner) { l ->
                        binding.fragmentNoteForegroundViewGrey.visibility = View.INVISIBLE
                        adapter.submitList(
                            when (selectTag) {
                                "全部笔记" -> l
                                "收藏" -> l.filter { n ->
                                    n.star
                                }
                                else -> l.filter { n ->
                                    n.tag == selectTag
                                }
                            }
                        )
                        binding.fragmentNoteTvNoteNull.visibility =
                            if (l.isEmpty()) View.VISIBLE else View.INVISIBLE
                        binding.fragmentNoteIvNoteNull.visibility =
                            if (l.isEmpty()) View.VISIBLE else View.INVISIBLE
                    }
                } else {
                    binding.fragmentNoteIvNoteNull.visibility = View.INVISIBLE
                    binding.fragmentNoteTvNoteNull.visibility = View.INVISIBLE
                    binding.fragmentNoteForegroundViewGrey.visibility = View.VISIBLE
                    binding.fragmentNoteIvCancel.visibility = View.GONE
                    if (!isFirst) {
                        adapter.submitList(list)
                    } else isFirst = false
                }
            }
        }
        binding.fragmentNoteIvCancel.setOnClickListener {
            binding.fragmentNoteEtSearch.setText("")
        }
        binding.fragmentNoteForegroundViewGrey.setOnClickListener {
            noteViewModel.exitSearchMode()
        }
    }

    private fun subscribe() {
        mainViewModel.title.observe(viewLifecycleOwner) {
            selectTag = it
            showNoteByTag(it)
        }
        mainViewModel.selectAll.observe(viewLifecycleOwner) { f ->
            binding.fragmentNoteRv.children.forEach {
                it.findViewById<CheckBox>(R.id.recycle_item_note_delete_box).isChecked = f
            }
        }
        mainViewModel.deleteMode.observe(viewLifecycleOwner) {
            searchBarAdapt(it)
            adapter.mainViewModel = mainViewModel
            adapter.notifyDataSetChanged()
        }
        noteViewModel.isSearchMode.observe(viewLifecycleOwner) {
            binding.viewModel = noteViewModel
            if (!it) {
                binding.fragmentNoteEtSearch.clearFocus()
                activity?.let { activity ->
                    activity.window.peekDecorView()?.let {
                        (activity.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                            it.windowToken,
                            0
                        )
                    }
                }
            }
        }
    }

    private fun searchBarAdapt(isDelete: Boolean) {
        binding.apply {
            if (isDelete) {
                fragmentNoteEtSearch.alpha = 0.5f
                fragmentNoteEtSearch.isCursorVisible = false
                fragmentNoteIvSearch.alpha = 0.5f
            } else {
                fragmentNoteEtSearch.alpha = 1f
                fragmentNoteEtSearch.isCursorVisible = true
                fragmentNoteIvSearch.alpha = 1f
            }
        }
    }

    private fun showNoteByTag(tag: String) {
        (if (tag == "全部笔记") noteViewModel.getNotes() else noteViewModel.getNotesByTag(tag)).observe(
            viewLifecycleOwner
        ) {
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
            list = it
            adapter.submitList(list)
        }
    }
}