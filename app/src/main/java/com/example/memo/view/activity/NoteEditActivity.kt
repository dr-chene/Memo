package com.example.memo.view.activity

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.memo.BaseActivity
import com.example.memo.R
import com.example.memo.databinding.ActivityNoteEditBinding

class NoteEditActivity : BaseActivity() {

    private lateinit var binding: ActivityNoteEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        makeStatusBarIconDark()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_edit)
    }
}