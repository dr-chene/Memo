package com.example.memo.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
Created by chene on @date 20-11-20 上午8:32
 **/
@Entity(tableName = "note")
data class Note (
    @PrimaryKey
    val createTime: Long,
    var title: String,
    var content: String,
    var changeTime: Long,
    var img: Boolean,
    var tag: String = NOTE_TAG_NULL
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