package com.example.memo.view.activity

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.style.*
import android.util.Log
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.memo.ext.pop
import com.example.memo.ext.push
import com.example.memo.model.bean.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import kotlin.collections.set

/**
Created by chene on @date 20-11-22 下午5:02
 **/
class NoteEditHelper(
    val operation: MutableMap<Location, List<CharacterStyle>>,
    val imgs: MutableMap<Int, String>
) {

    private val redoCache by inject(RedoCache::class.java) { parametersOf(6) }
    private val undoCache by inject(Cache::class.java) { parametersOf(6) }

    val isRedo: LiveData<Boolean>
        get() = _isRedo
    private val _isRedo: MutableLiveData<Boolean> = MutableLiveData(false)
    private fun setRedoEdit(r: Boolean) {
        _isRedo.postValue(r)
    }

    val isUndo: LiveData<Boolean>
        get() = _isUndo
    private val _isUndo: MutableLiveData<Boolean> = MutableLiveData(false)
    private fun setUndoEdit(r: Boolean) {
        _isUndo.postValue(r)
    }

    fun write(
        s: Editable,
        loc: Location,
        styles: List<CharacterStyle>,
        isLoad: Boolean,
        before: Int
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            synchronized(styles) {
                val count = loc.end - loc.start
                if (count > 0) {
                    if (!isLoad) {
                        operation[loc] = styles
                        Log.d(
                            "TAG_12",
                            "write: ${operation[loc]?.map { it.autoToString() }.toString()}"
                        )
                        operation.keys.forEach {
                            if (loc != it) {
                                if (loc.start <= it.start) {
                                    it.start += count
                                    it.end += count
                                } else if (loc.start < it.end) {
                                    it.end += count
                                }
                            }
                        }
                        undoCache.push(loc)
                        setUndoEdit(true)
                    }
                    operation.toSortedMap { l1, l2 ->
                        l1.start - l2.start
                    }
                    Log.d("TAG_12", "write operation size: ${operation.size}")
                    load(s)
                } else {
                    delete(loc.start - before, loc.start)
                    TODO("resume delete operation")
                }
            }
        }
    }

    fun redo(s: Editable) = synchronized(s) {
        redoCache.pop()?.let {
            undoCache.push(it)
            setUndoEdit(true)
            s.insert(it.start, redoCache.redoStr.pop())
            redoCache.redoStyles.pop()?.forEach { style ->
                s.setSpan(style, it.start, it.end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            if (redoCache.array.size == 0) {
                setRedoEdit(false)
            }
        }
    }

    fun undo(s: Editable) = synchronized(s) {
        undoCache.pop()?.let {
            operation[it]?.let { list ->
                redoCache.push(it, list, s.substring(it.start, it.end))
                setRedoEdit(true)
                s.delete(it.start, it.end)
                val count = it.end - it.start
                operation.keys.forEach { loc ->
                    if (it.start < loc.start) {
                        loc.start -= count
                        loc.end -= count
                    } else if (loc.end > it.start) {
                        loc.end -= count
                    }
                }
                operation.remove(it)
                if (undoCache.array.size == 0) {
                    setUndoEdit(false)
                }
            }
        }
    }

    fun clear() {
        redoCache.apply {
            array.clear()
            redoStyles.clear()
            redoStr.clear()
        }
        setRedoEdit(false)
        undoCache.array.clear()
        setUndoEdit(false)
    }

    fun insertImg(
        context: Context,
        uri: Uri?,
        s: Editable,
        start: Int,
        screenWidth: Int,
        isLoad: Boolean
    ) {
        if (uri == null) {
            Toast.makeText(context, "获取图片失败", Toast.LENGTH_SHORT).show()
            return
        }
        val job = CoroutineScope(Dispatchers.IO).async {
            Glide.with(context)
                .load(uri)
                .submit()
                .get()
                .let {
                    val mWidth = it.intrinsicWidth
                    val mHeight = it.intrinsicHeight
                    val screenHeight = (1f * screenWidth / mWidth * mHeight).toInt()
                    it.toBitmap(screenWidth, screenHeight)
                }
        }
        CoroutineScope(Dispatchers.Main).launch {
            val imgSpan = ImageSpan(context, job.await())
            val tempUrl = "<img src=\"${uri.path}\" />"
            if (!isLoad) {
                s.insert(start, tempUrl)
                uri.path?.let {
                    val path = if (it.startsWith("/document")) {
                        "/storage/emulated/0/" + uri.lastPathSegment?.substring(8)
                    } else it
                    imgs[start] = path
                }
            }
            s.setSpan(imgSpan, start, start + tempUrl.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    fun loadImgs(context: Context, s: Editable, screenWidth: Int) {
        imgs.keys.forEach {
            imgs[it]?.apply {
                insertImg(context, getImageContentUri(context, this), s, it, screenWidth, true)
            }
        }
    }

    private fun getImageContentUri(context: Context, path: String): Uri? {
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID), MediaStore.Images.Media.DATA + "=? ",
            arrayOf(path), null
        )
        if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            cursor.close()
            return Uri.withAppendedPath(baseUri, "$id")
        } else {
            cursor?.close()
            return if (File(path).exists()) {
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DATA, path)
                }
                context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
            } else {
                null
            }
        }

    }

    private fun load(s: Editable) {
        operation.keys.forEach { k ->
            operation[k]?.forEach { v ->
                s.setSpan(v, k.start, k.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun delete(dStart: Int, dEnd: Int) {
        operation.keys.forEach {
            if (it.end >= dEnd) {
                it.end -= dEnd - dStart
                if (it.start > dStart) {
                    it.start = dEnd
                }
            } else if (it.end > dStart) {
                it.end = dStart
            }
        }
        val delete = operation.keys.filter {
            it.start < 0 || it.end < 0
        }
        delete.forEach {
            operation.remove(it)
        }
    }

    open class Cache(
        private val capacity: Int
    ) {
        val array: MutableList<Location> = mutableListOf()

        fun push(v: Location) {
            array.push(v, capacity)
        }

        fun pop(): Location? = array.pop()
    }

    class RedoCache(
        private val cap: Int
    ) : Cache(cap) {
        val redoStyles: MutableList<List<CharacterStyle>> = mutableListOf()
        val redoStr: MutableList<String> = mutableListOf()

        fun push(v: Location, spans: List<CharacterStyle>, str: String) {
            array.push(v, cap)
            redoStyles.push(spans, cap)
            redoStr.push(str, cap)
        }
    }
}

fun CharacterStyle.autoToString() =
    when (this) {
        is ForegroundColorSpan -> {
            "color ${this.foregroundColor}"
        }
        is RelativeSizeSpan -> {
            "size ${this.sizeChange}"
        }
        is UnderlineSpan -> {
            "underline"
        }
        is StyleSpan -> {
            "style ${this.style}"
        }
        else -> ""
    }