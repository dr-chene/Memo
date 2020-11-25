package com.example.memo.viewmodel

import androidx.lifecycle.ViewModel
import com.example.memo.model.bean.Note
import com.example.memo.model.repository.NoteRepository

/**
Created by chene on @date 20-11-20 上午8:58
 **/
class NoteViwModel internal constructor(
    private val noteRepository: NoteRepository
) : ViewModel() {

    fun getNotes() = noteRepository.getAllNotes()

    fun getNotesByTag(tag: String) = noteRepository.getNotesByTag(tag)

    fun getNoteByTime(time: Long) = noteRepository.getNoteByTime(time)

    fun getStarNote() = noteRepository.getStarNote(true)

    suspend fun insertNote(note: Note){
        noteRepository.insertNote(note)
    }

    suspend fun deleteNotes(notes: List<Note>){
        notes.forEach {
            noteRepository.deleteNote(it)
        }
    }
}