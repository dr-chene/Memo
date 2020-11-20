package com.example.memo.di

import com.example.memo.viewmodel.MainActivityViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
Created by chene on @date 20-11-20 上午9:46
 **/
val mainModule = module {

    viewModel { MainActivityViewModel() }
}