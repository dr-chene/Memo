package com.example.memo.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.memo.model.bean.Tag.Companion.TAG_NULL

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
    var star: Boolean,
    var tag: String = TAG_NULL
)