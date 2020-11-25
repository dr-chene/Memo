package com.example.memo.model.bean

import android.text.style.*
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent

/**
Created by chene on @date 20-11-23 下午4:59
 **/
data class NoteContent(
    val content: String,
    val styles: MutableMap<Location, List<String>>
)

fun String.generateNoteContent(): NoteContent = GsonBuilder().enableComplexMapKeySerialization().create().fromJson(this, NoteContent::class.java)

fun NoteContent.generateString(): String {
    val str = GsonBuilder().enableComplexMapKeySerialization().create().toJson(this)
    Log.d("TAG_10", str)
    return str
}

fun MutableMap<Location, List<String>>.generateStyles(): MutableMap<Location, List<CharacterStyle>> {
    val map = mutableMapOf<Location, List<CharacterStyle>>()
    this.keys.forEach {
        this[it]?.let { list ->
            map[it] = list.map { s ->
                s.generateStyle()
            }
        }
    }
    return map.toSortedMap{ k1, k2 ->
        k1.start - k2.start
    }
}

fun String.generateStyle(): CharacterStyle {
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