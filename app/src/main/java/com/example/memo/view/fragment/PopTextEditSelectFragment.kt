package com.example.memo.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.memo.databinding.FragmentPopTextEditSelectBinding
import com.example.memo.view.ColorCircleView

/**
Created by chene on @date 20-11-21 下午3:16
 **/
class PopTextEditSelectFragment : Fragment() {

    private lateinit var binding: FragmentPopTextEditSelectBinding
    private lateinit var curSelect: ColorCircleView

    private val colorSelect = View.OnClickListener {
        curSelect.unSelected()
        curSelect = it as ColorCircleView
        curSelect.selected()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPopTextEditSelectBinding.inflate(inflater, container, false)
        context ?: return binding.root

        initView()

        return binding.root
    }

    private fun initView() {
        binding.colorSelect = colorSelect

        binding.apply {
            popTextEditIvBlack.color = "#000000"
            popTextEditIvPurple.color = "#9C27B0"
            popTextEditIvCyan.color = "#00BCD4"
            popTextEditIvBlue.color = "#03A9F4"
            popTextEditIvGreen.color = "#8BC34A"
            popTextEditIvYellow.color = "#FFEB3B"
            popTextEditIvRed.color = "#f44336"
        }
        binding.popTextEditIvBlack.length.observe(viewLifecycleOwner){
            binding.popTextEditIvBlack.selected()
            curSelect = binding.popTextEditIvBlack
        }
    }
}