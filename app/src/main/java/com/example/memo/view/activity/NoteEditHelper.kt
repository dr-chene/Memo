package com.example.memo.view.activity

import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.style.*
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.memo.ext.pop
import com.example.memo.ext.push
import com.example.memo.model.bean.Location
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject
import kotlin.collections.set

/**
Created by chene on @date 20-11-22 下午5:02
 **/
class NoteEditHelper(
    val operation: MutableMap<Location, List<CharacterStyle>>
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

    fun write(s: Editable, loc: Location, styles: List<CharacterStyle>, isLoad: Boolean) {
        if (!isLoad) {
            operation[loc] = styles
            Log.d("TAG_12", "write: ${operation[loc]?.map { it.autoToString() }.toString()}")
            val count = loc.end - loc.start
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
    }

    fun redo(s: Editable) = synchronized(s) {
        redoCache.pop()?.let {
            undoCache.push(it)
            setUndoEdit(true)
            s.insert(it.start, redoCache.redoStr.pop())
            redoCache.redoSpans.pop()?.forEach { style ->
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

    private fun load(s: Editable) {
        var i = 0
        Log.d("TAG_15", "load: ${operation.size}")
        operation.keys.forEach { k ->
            Log.d("TAG_14", "load ${i++} ${k.start} ${k.end}: ${operation[k]}")
            operation[k]?.forEach { v ->
                Log.d("TAG_14", "loading $i")
                s.setSpan(v, k.start, k.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
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
        val redoSpans: MutableList<List<CharacterStyle>> = mutableListOf()
        val redoStr: MutableList<String> = mutableListOf()

        fun push(v: Location, spans: List<CharacterStyle>, str: String) {
            array.push(v, cap)
            redoSpans.push(spans, cap)
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