package com.example.memo.view.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.memo.databinding.FragmentTextEditSelectBinding
import com.example.memo.view.ColorCircleView
import com.example.memo.viewmodel.TextEditSelectViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

/**
Created by chene on @date 20-11-21 下午3:16
 **/
class TextEditSelectFragment : Fragment() {

    private lateinit var binding: FragmentTextEditSelectBinding
    private val textEditSelectViewModel: TextEditSelectViewModel by sharedViewModel()
    private lateinit var curSelect: ColorCircleView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTextEditSelectBinding.inflate(inflater, container, false)
        context ?: return binding.root

        initView()
        subscribe()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            listOf(
                popTextEditIvBlack,
                popTextEditIvPurple,
                popTextEditIvCyan,
                popTextEditIvBlue,
                popTextEditIvGreen,
                popTextEditIvYellow,
                popTextEditIvRed
            ).forEach {
                if (curSelect !== it) {
                    curSelect.unSelected()
                }
            }
        }
    }

    private fun initView() {
        Log.d("TAG_f", "initView: into")
        binding.apply {
            popTextEditIvBlack.color = "#000000"
            popTextEditIvPurple.color = "#9C27B0"
            popTextEditIvCyan.color = "#00BCD4"
            popTextEditIvBlue.color = "#03A9F4"
            popTextEditIvGreen.color = "#8BC34A"
            popTextEditIvYellow.color = "#FFEB3B"
            popTextEditIvRed.color = "#f44336"
        }

        binding.colorSelect = View.OnClickListener {
            Log.d("TAG_f", "initView: click")
            curSelect.unSelected()
            curSelect = it as ColorCircleView
            textEditSelectViewModel.colorSelect(Color.parseColor(curSelect.color))
            curSelect.selected()
        }

        binding.popTextEditIvBlack.length.observe(viewLifecycleOwner) {
            binding.popTextEditIvBlack.selected()
            curSelect = binding.popTextEditIvBlack
        }

        binding.popTextEditIvBold.setOnClickListener {
            textEditSelectViewModel.boldSelect(!it.isSelected)
        }
        binding.popTextEditIvItalic.setOnClickListener {
            textEditSelectViewModel.italicSelect(!it.isSelected)
        }
        binding.popTextEditIvUnderline.setOnClickListener {
            textEditSelectViewModel.underLineSelect(!it.isSelected)
        }
        binding.fragmentPopTextEditSelectSeekBar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    textEditSelectViewModel.setTextSize((progress - 3) / 10f)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        binding.popTextEditIvAlignLeft.setOnClickListener {
            textEditSelectViewModel.setAlignDirection(TextView.TEXT_ALIGNMENT_TEXT_START)
        }
        binding.popTextEditIvAlignCenter.setOnClickListener {
            textEditSelectViewModel.setAlignDirection(TextView.TEXT_ALIGNMENT_CENTER)
        }
        binding.popTextEditIvAlignRight.setOnClickListener {
            textEditSelectViewModel.setAlignDirection(TextView.TEXT_ALIGNMENT_TEXT_END)
        }
        binding.setCloseClick {
            textEditSelectViewModel.setSelectShow(false)
        }
    }

    private fun subscribe() {
        var curSelect = binding.popTextEditIvAlignLeft
        textEditSelectViewModel.align.observe(viewLifecycleOwner) {
            curSelect.isSelected = false
            when (it) {
                TextView.TEXT_ALIGNMENT_TEXT_START -> {
                    binding.popTextEditIvAlignLeft.isSelected = true
                    curSelect = binding.popTextEditIvAlignLeft
                }
                TextView.TEXT_ALIGNMENT_CENTER -> {
                    binding.popTextEditIvAlignCenter.isSelected = true
                    curSelect = binding.popTextEditIvAlignCenter
                }
                TextView.TEXT_ALIGNMENT_TEXT_END -> {
                    binding.popTextEditIvAlignRight.isSelected = true
                    curSelect = binding.popTextEditIvAlignRight
                }
            }
        }
        textEditSelectViewModel.apply {
            isBold.observe(viewLifecycleOwner) {
                binding.popTextEditIvBold.isSelected = it
            }
            isItalic.observe(viewLifecycleOwner) {
                binding.popTextEditIvItalic.isSelected = it
            }
            isUnderLine.observe(viewLifecycleOwner) {
                binding.popTextEditIvUnderline.isSelected = it
            }
        }
    }
}