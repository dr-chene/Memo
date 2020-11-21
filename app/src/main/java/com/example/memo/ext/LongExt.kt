package com.example.memo.ext

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
Created by chene on @date 20-11-20 下午2:02
 **/
@SuppressLint("SimpleDateFormat")
fun Long.toNoteDate(): String{
    val cur = System.currentTimeMillis()
    return if (abs(this - cur) <= 300000) "刚刚"
    else {
        val date = Date(this)
        val sdFormatter = SimpleDateFormat("MMdd HH:mm")
        sdFormatter.format(date)
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.day(): String{
    val cur = System.currentTimeMillis()
    return when(cur - this){
        in 0..86400000 -> {
            "今天"
        }
        in 86400000..172800000 -> {
            "昨天"
        }
        else -> {
            val date = Date(this)
            val sdFormatter = SimpleDateFormat("MM-dd")
            sdFormatter.format(date)
        }
    }
}

@SuppressLint("SimpleDateFormat")
fun Long.time(): String{
    val date = Date(this)
    val sdFormatter = SimpleDateFormat("HH:mm")
    return sdFormatter.format(date)
}

fun Long.toDoDate(): String{
    val cur = System.currentTimeMillis()
    return when(abs(this - cur)){
        cur ->{
            "无日期"
        }
        in 0..86400000 -> {
            "今天"
        }
        in 86400000..172800000 -> {
            "明天"
        }
        else -> {
            "以后"
        }
    }
}
