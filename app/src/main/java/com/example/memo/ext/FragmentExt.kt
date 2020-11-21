package com.example.memo.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.memo.R

/**
Created by chene on @date 20-11-20 上午11:23
 **/
fun FragmentManager.navTo(fragment: Fragment){
    popBackStack()
    beginTransaction().addToBackStack(null)
        .replace(R.id.activity_main_fragment_container, fragment)
        .commit()
}