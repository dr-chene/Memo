package com.example.memo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
Created by chene on @date 20-11-20 上午9:00
 **/
class MainActivityViewModel : ViewModel() {
    val tabSelected: LiveData<Int>
        get() = _tabSelected
    private val _tabSelected: MutableLiveData<Int> = MutableLiveData(0)
    fun selectTab(tab: Int) {
        _tabSelected.postValue(tab)
        selectTag(if (tab == 0) "全部笔记" else "全部待办")
    }

    val title: LiveData<String>
        get() = _title
    private val _title: MutableLiveData<String> =
        MutableLiveData(if (_tabSelected.value == 0) "全部笔记" else "全部待办")

    fun selectTag(tag: String) {
        _title.postValue(tag)
    }

    val subTitle: LiveData<String>
        get() = _subTitle
    private val _subTitle: MutableLiveData<String> = MutableLiveData("")
    fun sumNum(sum: Int) {
        _subTitle.postValue("$sum 条" + if (_tabSelected.value == 0) "笔记" else "待办")
    }

    val deleteMode: LiveData<Boolean>
        get() = _deleteMode
    private val _deleteMode = MutableLiveData(false)
    fun enterDeleteMode() {
        _deleteMode.postValue(true)
    }

    fun exitDeleteMode() {
        _deleteMode.postValue(false)
    }

    val selectAll: LiveData<Boolean>
        get() = _selectAll
    private val _selectAll = MutableLiveData(false)
    fun selectAll(flag: Boolean) {
        _selectAll.postValue(flag)
    }

    val deleteList: LiveData<Set<Long>>
        get() = _deleteList
    private val _deleteList = MutableLiveData<Set<Long>>(setOf())
    fun select(createTime: Long) = synchronized(deleteSet) {
        deleteSet.add(createTime)
        _deleteList.postValue(deleteSet)
    }

    fun unSelect(createTime: Long) = synchronized(deleteSet) {
        deleteSet.remove(createTime)
        _deleteList.postValue(deleteSet)
    }

    val deleteSet = mutableSetOf<Long>()
}