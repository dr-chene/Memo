package com.example.memo.view.adapter

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.memo.model.bean.Note
import com.example.memo.model.bean.toNoteImages

/**
Created by chene on @date 20-11-20 下午3:55
 **/
@BindingAdapter("bindNoteCover")
fun bindNoteCover(view: ImageView, note: Note) {
    if (view.visibility != View.VISIBLE) {
        return
    }
    val uriString = note.imgs.toNoteImages().images.toSortedMap { k1, k2 ->
        k1 - k2
    }.let {
        it[it.firstKey()]
    }
    Glide.with(view.context)
        .load(Uri.parse(uriString))
        .centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(view)
}