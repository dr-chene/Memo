package com.example.memo.model.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.memo.model.bean.ToDo

/**
Created by chene on @date 20-11-20 下午2:38
 **/
@Dao
interface ToDoDao {

    @Query("SELECT * FROM todo WHERE toDoTime = :date ORDER BY createTime DESC")
    fun getToDoByDate(date: String): LiveData<List<ToDo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToDo(toDo: ToDo)

    @Query("DELETE FROM todo WHERE createTime = :time")
    suspend fun deleteToDoByCreateTime(time: Long)
}