package com.example.memo.model.repository

import com.example.memo.model.bean.Note

/**
Created by chene on @date 20-11-20 下午2:48
 **/
class NoteRepository(
    private val noteDao: NoteDao
) {

    fun getAllNotes() = noteDao.getAllNotes()

    fun getNotesByTag(tag: String) = noteDao.getNotesByTag(tag)

    fun getNoteByTime(time: Long) = noteDao.getNoteByCreateTime(time)

    fun getStarNote(star: Boolean) = noteDao.getStarNote(star)

    fun getNoteByString(str: String) = noteDao.getNoteByString(str)

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun deleteNoteByTime(time: Long) {
        noteDao.deleteNoteByCreateTime(time)
    }

}