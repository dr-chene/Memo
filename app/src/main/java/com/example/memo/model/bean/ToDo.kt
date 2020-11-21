package com.example.memo.model.bean

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.memo.ext.toDoDate

/**
Created by chene on @date 20-11-20 上午8:42
 **/
@Entity(tableName = "todo")
data class ToDo (
    @PrimaryKey
    val createTime: Long,
    val content: String,
    val important: Boolean = false,
    var isDone: Boolean = false,
    val toDoTime: Long = 0,
    val remark: String? = null,
    val repeat: String = REPEAT_NULL,
    val tag: String = DO_TAG_NULL
) {

    var date: String = if (isDone) DATE_DONE else toDoTime.toDoDate()
    fun done(){
        isDone = true
        date = DATE_DONE
    }
    fun cancelDone(){
        isDone = false
        date = toDoTime.toDoDate()
    }

    companion object {

        const val DO_TAG_WORK = "工作"
        const val DO_TAG_PERSONAL = "个人"
        const val DO_TAG_SHOP = "购物"
        const val DO_TAG_NULL = "未分类"

        const val REPEAT_DAY = "每天"
        const val REPEAT_WEEK = "每周"
        const val REPEAT_MONTH = "每月"
        const val REPEAT_YEAR = "每年"
        const val REPEAT_NULL = "不重复"

        const val DATE_TODAY = "今天"
        const val DATE_TOMORROW = "明天"
        const val DATE_LATER = "以后"
        const val DATE_DONE = "已完成"
        const val DATE_NULL = "无日期"
    }
}