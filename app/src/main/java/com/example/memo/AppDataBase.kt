package com.example.memo

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.memo.model.bean.ToDo
import com.example.memo.model.bean.Note
import com.example.memo.model.repository.NoteDao
import com.example.memo.model.repository.ToDoDao

/**
Created by chene on @date 20-11-20 上午11:07
 **/
@Database(entities = [Note::class, ToDo::class], version = 1, exportSchema = false)
abstract class AppDataBase : RoomDatabase(){

    abstract fun noteDao(): NoteDao
    abstract fun toDoDao(): ToDoDao

    companion object {

        private const val DATA_BASE_NAME = "memo.db"

        fun buildDatabase(context: Context): AppDataBase{
            return Room.databaseBuilder(context, AppDataBase::class.java, DATA_BASE_NAME)
                .build()
        }
    }
}