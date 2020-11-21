package com.example.memo.di

import com.example.memo.AppDataBase
import com.example.memo.model.repository.NoteRepository
import com.example.memo.viewmodel.MainActivityViewModel
import com.example.memo.viewmodel.NoteViwModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
Created by chene on @date 20-11-20 上午9:46
 **/
val mainModule = module {

    viewModel { MainActivityViewModel() }
}

val noteModule = module {

    single { AppDataBase.buildDatabase(get()) }

    single { NoteRepository((get() as AppDataBase).noteDao()) }

    viewModel { NoteViwModel(get()) }
}