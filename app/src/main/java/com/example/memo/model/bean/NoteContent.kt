package com.example.memo.model.bean

import android.text.style.*
import android.util.Log
import com.google.gson.GsonBuilder

/**
Created by chene on @date 20-11-23 下午4:59
 **/
data class NoteStyles(
    val styles: MutableMap<Location, List<String>>
)

fun String.toNoteStyles(): NoteStyles =
    GsonBuilder().enableComplexMapKeySerialization().create().fromJson(this, NoteStyles::class.java)

fun NoteStyles.generateString(): String {
    val str = GsonBuilder().enableComplexMapKeySerialization().create().toJson(this)
    Log.d("TAG_10", str)
    return str
}

fun MutableMap<Location, List<String>>.toStyles(): MutableMap<Location, List<CharacterStyle>> {
    val map = mutableMapOf<Location, List<CharacterStyle>>()
    this.keys.forEach {
        this[it]?.let { list ->
            map[it] = list.map { s ->
                s.toStyle()
            }
        }
    }
    return map.toSortedMap { k1, k2 ->
        k1.start - k2.start
    }
}

fun String.toStyle(): CharacterStyle {
    val param = this.split(" ")
    return if (param.size < 2) {
        UnderlineSpan()
    } else {
        when (param[0]) {
            "color" -> {
                ForegroundColorSpan(param[1].toInt())
            }
            "size" -> {
                RelativeSizeSpan(param[1].toFloat())
            }
            "style" -> {
                StyleSpan(param[1].toInt())
            }
            else -> {
                throw Exception("autoGenerate desc failed")
            }
        }
    }
}