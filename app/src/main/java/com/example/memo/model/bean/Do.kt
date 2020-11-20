package com.example.memo.model.bean

/**
Created by chene on @date 20-11-20 上午8:42
 **/
data class Do (
    val content: String,
    val important: Boolean,
    val time: Long = 0,
    val remark: String? = null,
    val repeat: String = REPEAT_NULL,
    val tag: String = DO_TAG_NULL
) {

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
    }
}