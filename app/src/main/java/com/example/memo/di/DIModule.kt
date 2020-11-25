package com.example.memo.di


import android.graphics.drawable.Drawable
import android.text.style.*
import androidx.lifecycle.ViewModelStoreOwner
import com.example.memo.AppDataBase
import com.example.memo.model.bean.Location
import com.example.memo.model.bean.Tag
import com.example.memo.model.repository.NoteRepository
import com.example.memo.view.activity.NoteEditHelper
import com.example.memo.view.adapter.NoteRecyclerViewAdapter
import com.example.memo.view.adapter.TagRecyclerViewAdapter
import com.example.memo.view.fragment.NoteFragment
import com.example.memo.view.fragment.ToDoFragment
import com.example.memo.viewmodel.MainActivityViewModel
import com.example.memo.viewmodel.NoteViwModel
import com.example.memo.viewmodel.TextEditSelectViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
Created by chene on @date 20-11-20 上午9:46
 **/
val appDataBaseModule = module {

    single { AppDataBase.buildDatabase(get()) }
}

val mainActivityModule = module {

    single { NoteFragment() }

    single { ToDoFragment() }

    single { NoteRepository((get() as AppDataBase).noteDao()) }

    single { (p: MainActivityViewModel) -> TagRecyclerViewAdapter(p) }

    viewModel { NoteViwModel(get()) }

    viewModel { MainActivityViewModel() }

}

val noteFragmentModule = module {
    single { NoteRecyclerViewAdapter() }
}

val noteEditActivityModule = module {

    viewModel { TextEditSelectViewModel() }
}

val classModule = module {

    factory { (drawable: Drawable, tag: String, count: Int) -> Tag(drawable, tag, count) }
}