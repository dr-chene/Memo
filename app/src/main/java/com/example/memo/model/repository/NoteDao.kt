package com.example.memo.model.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.memo.model.bean.Note
import kotlinx.coroutines.flow.Flow

/**
Created by chene on @date 20-11-20 下午2:23
 **/
@Dao
interface NoteDao {

    @Query("SELECT * FROM note ORDER BY changeTime DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE tag = :tag ORDER BY changeTime DESC")
    fun getNotesByTag(tag: String): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE createTime = :createTime ORDER BY changeTime DESC")
    fun getNoteByCreateTime(createTime: Long): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE content LIKE '%' || :str || '%' OR title LIKE '%' || :str || '%' ORDER BY changeTime DESC")
    fun getNoteByString(str: String): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE star = :star ORDER BY changeTime DESC")
    fun getStarNote(star: Boolean): Flow<List<Note>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("DELETE FROM note WHERE createTime = :time")
    suspend fun deleteNoteByCreateTime(time: Long)
}