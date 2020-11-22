package com.example.memo.model.bean

import android.graphics.drawable.Drawable

/**
Created by chene on @date 20-11-22 上午10:16
 **/
data class Tag(
    val img: Drawable?,
    val tag: String,
    var count: Int
) {
    companion object {
        const val TAG_E_BOOK = "电子书"
        const val TAG_TRAVEL = "旅游"
        const val TAG_PERSONAL = "个人"
        const val TAG_LIFE = "生活"
        const val TAG_WORK = "工作"
        const val TAG_NULL = "未分类"
    }
}