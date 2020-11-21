package com.example.memo.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.memo.App
import com.example.memo.BaseActivity
import com.example.memo.R
import com.example.memo.databinding.ActivityMainBinding
import com.example.memo.ext.navTo
import com.example.memo.view.fragment.DoFragment
import com.example.memo.view.fragment.NoteFragment
import com.example.memo.viewmodel.MainActivityViewModel
import com.google.android.material.tabs.TabLayout
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModel()

    private val addClick = View.OnClickListener {
        when(viewModel.tabSelected.value){
            TAB_NOTE -> startActivity(Intent(App.context, NoteEditActivity::class.java))
            TAB_DO -> startActivity(Intent(App.context, DoEditActivity::class.java))
        }
    }
    private val tabSelectListener = object : TabLayout.OnTabSelectedListener{
        override fun onTabSelected(tab: TabLayout.Tab?) {
            tab?.let {
                when(it.text){
                    resources.getString(R.string.activity_main_tab_note) -> {
                        supportFragmentManager.navTo(NoteFragment())
                        viewModel.selectTab(TAB_NOTE)
                    }
                    resources.getString(R.string.activity_main_tab_do) -> {
                        supportFragmentManager.navTo(DoFragment())
                        viewModel.selectTab(TAB_DO)
                    }
                }
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}

        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        makeStatusBarIconDark()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initView()
    }

    private fun initView() {
        setSupportActionBar(binding.activityMainToolbar)
        binding.viewmodel = viewModel
        binding.addClick = addClick
        binding.activityMainTab.addOnTabSelectedListener(tabSelectListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when(item.itemId){

        }
        return true
    }

    companion object {
        const val TAB_NOTE = 0
        const val TAB_DO = 1
    }
}