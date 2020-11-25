package com.example.memo.ext

import android.text.style.CharacterStyle

/**
Created by chene on @date 20-11-23 下午2:45
 **/
fun <T> MutableList<T>.push(t: T, c: Int) = synchronized(this){
    while (this.size >= c){
        this.removeAt(0)
    }
    this.add(t)
}

fun <T> MutableList<T>.pop(): T? = synchronized(this){
    if (this.size >= 1){
        this.removeAt(this.size - 1)
    } else {
        return null
    }
}