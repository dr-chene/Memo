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
    val textColor: LiveData<Int>
        get() = _textColor
    private var _textColor: MutableLiveData<Int> = MutableLiveData(Color.BLACK)
    fun colorSelect(color: Int){
        _textColor.postValue(color)
    }

    val isBold: LiveData<Boolean>
        get() = _isBold
    private var _isBold: MutableLiveData<Boolean> =  MutableLiveData(false)
    fun boldSelect(isSelect: Boolean){
        _isBold.postValue(isSelect)
    }

    val isItalic: LiveData<Boolean>
        get() = _isItalic
    private var _isItalic: MutableLiveData<Boolean> =  MutableLiveData(false)
    fun italicSelect(isSelect: Boolean){
        _isItalic.postValue(isSelect)
    }

    val isUnderLine: LiveData<Boolean>
        get() = _isUnderLine
    private var _isUnderLine: MutableLiveData<Boolean> = MutableLiveData(false)
    fun underLineSelect(isSelect: Boolean){
        _isUnderLine.postValue(isSelect)
    }

    val textSize: LiveData<Float>
        get() = _textSize
    private var _textSize: MutableLiveData<Float> = MutableLiveData(1.0f)
    fun setTextSize(textSize: Float){
        _textSize.postValue(1 + textSize)
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