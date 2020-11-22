package com.example.memo.viewmodel

import android.graphics.Color
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
Created by chene on @date 20-11-21 下午4:59
 **/
class TextEditSelectViewModel : ViewModel(){
    val textColor: Int
        get() = _textColor
    private var _textColor: Int = Color.BLACK
    fun colorSelect(color: Int){
        _textColor = color
    }

    val isBold: Boolean
        get() = _isBold
    private var _isBold: Boolean =  false
    fun boldSelect(isSelect: Boolean){
        _isBold = isSelect
    }

    val isItalic: Boolean
        get() = _isItalic
    private var _isItalic: Boolean =  false
    fun italicSelect(isSelect: Boolean){
        _isItalic = isSelect
    }

    val isUnderLine: Boolean
        get() = _isUnderLine
    private var _isUnderLine: Boolean = false
    fun underLineSelect(isSelect: Boolean){
        _isUnderLine = isSelect
    }

    val textSize: Float
        get() = _textSize
    private var _textSize: Float = 1.0f
    fun setTextSize(textSize: Float){
        _textSize = 1 + textSize
    }

    val align: LiveData<Int>
        get() = _align
    private var _align: MutableLiveData<Int> = MutableLiveData(TextView.TEXT_ALIGNMENT_TEXT_START)
    fun setAlignDirection(align: Int){
        _align.postValue(align)
    }

    val selectShow: LiveData<Boolean>
        get() = _selectShow
    private var _selectShow: MutableLiveData<Boolean> = MutableLiveData(false)
    fun setSelectShow(show: Boolean){
        _selectShow.postValue(show)
    }
}