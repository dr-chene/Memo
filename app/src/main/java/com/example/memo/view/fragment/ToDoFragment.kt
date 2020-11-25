package com.example.memo.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memo.databinding.FragmentDoBinding

/**
Created by chene on @date 20-11-19 下午2:50
 **/
class ToDoFragment : Fragment() {

    private lateinit var binding: FragmentDoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoBinding.inflate(inflater, container, false)
        context ?: return binding.root

        return binding.root
    }
}