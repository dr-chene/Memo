package com.example.memo.model.bean

/**
Created by chene on @date 20-11-20 上午8:32
 **/
data class Note (
    val title: String,
    val content: String,
    val time: Long,
    val tag: String = NOTE_TAG_NULL
){

    companion object {
        const val NOTE_TAG_E_BOOK = "电子书"
        const val NOTE_TAG_TRAVEL = "旅游"
        const val NOTE_TAG_PERSONAL = "个人"
        const val NOTE_TAG_LIVE = "生活"
        const val NOTE_TAG_WORK = "工作"
        const val NOTE_TAG_NULL = "未分类"
    }
}