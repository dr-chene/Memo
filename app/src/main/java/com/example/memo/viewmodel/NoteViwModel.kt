package com.example.memo.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.memo.model.bean.Note
import com.example.memo.model.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest

/**
Created by chene on @date 20-11-20 上午8:58
 **/
class NoteViwModel internal constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    val isSearchMode: LiveData<Boolean>
        get() = _isSearchMode
    private val _isSearchMode = MutableLiveData(false)
    fun enterSearchMode() {
        _isSearchMode.postValue(true)
    }

    fun exitSearchMode() {
        _isSearchMode.postValue(false)
    }

    fun getNotes() = liveData {
        noteRepository.getAllNotes().collectLatest {
            emit(it)
        }
    }

    fun getNotesByTag(tag: String) =
        if (tag == "收藏") getStarNote() else liveData {
            noteRepository.getNotesByTag(tag).collectLatest {
                emit(it)
            }
        }

    fun getNoteByTime(time: Long) = liveData {
        noteRepository.getNoteByTime(time).collectLatest {
            emit(it)
        }
    }

    fun getStarNote() = liveData {
        noteRepository.getStarNote(true).collectLatest {
            emit(it)
        }
    }

    fun getSearchNote(str: String) = liveData {
        noteRepository.getNoteByString(str).collectLatest {
            emit(it)
        }
    }

    suspend fun insertNote(note: Note) {
        noteRepository.insertNote(note)
    }

    suspend fun deleteNotesByTime(times: Set<Long>) {
        times.forEach {
            deleteNoteByTime(it)
        }
    }

    suspend fun deleteNoteByTime(time: Long) {
        noteRepository.deleteNoteByTime(time)
    }
}