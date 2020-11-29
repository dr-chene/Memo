package com.example.memo.view.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import com.example.memo.App
import com.example.memo.BaseActivity
import com.example.memo.R
import com.example.memo.databinding.ActivityMainBinding
import com.example.memo.databinding.PopWindowTagBinding
import com.example.memo.ext.navTo
import com.example.memo.model.bean.Tag
import com.example.memo.view.adapter.TagRecyclerViewAdapter
import com.example.memo.view.fragment.NoteFragment
import com.example.memo.view.fragment.ToDoFragment
import com.example.memo.viewmodel.MainActivityViewModel
import com.example.memo.viewmodel.NoteViwModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tagsBinding: PopWindowTagBinding
    private val mainViewModel: MainActivityViewModel by viewModel()
    private val noteViewModel: NoteViwModel by viewModel()
    private val noteFragment: NoteFragment by inject()
    private val todoFragment: ToDoFragment by inject()
    private lateinit var allTag: Tag
    private lateinit var starTag: Tag
    private lateinit var eBookTag: Tag
    private lateinit var travelTag: Tag
    private lateinit var personalTag: Tag
    private lateinit var lifeTag: Tag
    private lateinit var workTag: Tag
    private lateinit var nullTag: Tag
    private lateinit var tags: List<Tag>
    private lateinit var popTag: PopupWindow
    private var isRotate = false
    private var isDelete = false
    private var deleteCount = 0
    private lateinit var tagsAdapter: TagRecyclerViewAdapter
    private val toRotateAnim = AnimationUtils.loadAnimation(App.context, R.anim.rotate_to).apply {
        fillAfter = true
    }
    private val backRotateAnim =
        AnimationUtils.loadAnimation(App.context, R.anim.rotate_back).apply {
            fillAfter = true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        makeStatusBarIconDark()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initView()
        subscribe()
    }

    private fun initView() {
        setSupportActionBar(binding.activityMainToolbar)
        tabSelect("笔记")
        binding.viewmodel = mainViewModel
        binding.activityMainFabAdd.setOnClickListener {
            when (mainViewModel.tabSelected.value) {
                TAB_NOTE -> startActivity(Intent(App.context, NoteEditActivity::class.java))
                TAB_DO -> startActivity(Intent(App.context, DoEditActivity::class.java))
            }
        }
        binding.activityMainIncludeToolbar.toolbarTvTitle.setOnClickListener {
            showTags()
            binding.activityMainIncludeToolbar.toolbarIvDown.startAnimation(
                if (isRotate) backRotateAnim else toRotateAnim
            )
            isRotate = !isRotate
        }
        binding.activityMainTabNote.setOnClickListener {
            tabSelect(binding.activityMainTabTvNote.text.toString())
        }
        binding.activityMainTabTodo.setOnClickListener {
            tabSelect(binding.activityMainTabTvTodo.text.toString())
        }
        binding.activityMainIncludeToolbar.toolbarIvExit.setOnClickListener {
            mainViewModel.exitDeleteMode()
        }
        initTags()
    }

    private fun subscribe() {
        noteViewModel.getNotes().observe(this) {
            allTag.count = it.size
            tagsBinding.popWindowTagHead.popWindowTagAll.tag = allTag
            mainViewModel.selectTag(allTag.tag)
            mainViewModel.sumNum(allTag.count)
        }
        noteViewModel.getStarNote().observe(this) {
            starTag.count = it.size
            tagsBinding.popWindowTagHead.popWindowTagStar.tag = starTag
        }
        tags.forEach { tag ->
            noteViewModel.getNotesByTag(tag.tag).observe(this) {
                tag.count = it.size
                tagsAdapter.notifyDataSetChanged()
            }
        }
        mainViewModel.title.observe(this) {
            binding.activityMainIncludeToolbar.toolbarTvTitle.text = it
            popTag.dismiss()
        }
        mainViewModel.subTitle.observe(this) {
            binding.activityMainIncludeToolbar.toolbarTvSubtitle.text = it
        }
        mainViewModel.deleteMode.observe(this) {
            isDelete = it
            invalidateOptionsMenu()
            if (it) binding.apply {
                activityMainTabTvNote.setTextColor(Color.parseColor("#bfbfbf"))
                activityMainTabIvNote.isSelected = false
                activityMainTabIvNote.isClickable = false
                deleteTabAdapt(mainViewModel.deleteSet)
            } else {
                tabSelect(resources.getString(R.string.activity_main_tab_note))
            }
            binding.viewmodel = mainViewModel
            binding.activityMainIncludeToolbar.viewmodel = mainViewModel
        }
        mainViewModel.deleteList.observe(this) {
            if (isDelete) {
                deleteCount = it.size
                deleteTabAdapt(it)
            }
        }
        noteViewModel.isSearchMode.observe(this) {
            binding.activityMainFabAdd.visibility = if (it) View.GONE else View.VISIBLE
            binding.noteViewModel = noteViewModel
        }
    }

    private fun deleteTabAdapt(set: Set<Long>) {
        deleteSelectTitle(deleteCount)
        if (set.isNotEmpty()) {
            binding.apply {
                activityMainTabTvNote.setTextColor(Color.BLACK)
                activityMainTabIvNote.isSelected = true
                activityMainTabIvNote.isClickable = true
            }
        } else {
            binding.apply {
                activityMainTabTvNote.setTextColor(Color.parseColor("#bfbfbf"))
                activityMainTabIvNote.isSelected = false
                activityMainTabIvNote.isClickable = false
            }
        }
        mainViewModel.title.value?.let { tag ->
            if (set.size == itemCount(tag)) {
                binding.activityMainTabTvTodo.setText(R.string.activity_main_tab_cancel_select_all)
                binding.activityMainTabTvTodo.setTextColor(Color.parseColor("#2962FF"))
                binding.activityMainTabIvTodo.isSelected = true
            } else {
                binding.activityMainTabTvTodo.setText(R.string.activity_main_tab_select_all)
                binding.activityMainTabTvTodo.setTextColor(Color.BLACK)
                binding.activityMainTabIvTodo.isSelected = false
            }
        }
    }

    private fun tabSelect(tab: String) {
        when (tab) {
            resources.getString(R.string.activity_main_tab_note) -> {
                binding.activityMainTabIvNote.isSelected = true
                binding.activityMainTabTvNote.setTextColor(Color.parseColor("#2962FF"))
                binding.activityMainTabIvTodo.isSelected = false
                binding.activityMainTabTvTodo.setTextColor(Color.BLACK)
                supportFragmentManager.navTo(noteFragment)
                mainViewModel.selectTab(TAB_NOTE)
            }
            resources.getString(R.string.activity_main_tab_todo) -> {
                binding.activityMainTabIvNote.isSelected = false
                binding.activityMainTabTvNote.setTextColor(Color.BLACK)
                binding.activityMainTabIvTodo.isSelected = true
                binding.activityMainTabTvTodo.setTextColor(Color.parseColor("#2962FF"))
                supportFragmentManager.navTo(todoFragment)
                mainViewModel.selectTab(TAB_DO)
            }
            resources.getString(R.string.activity_main_tab_delete) -> {
                deleteConfirm()
            }
            resources.getString(R.string.activity_main_tab_select_all) -> {
                binding.apply {
                    activityMainTabIvTodo.isSelected = true
                    activityMainTabTvTodo.setText(R.string.activity_main_tab_cancel_select_all)
                    activityMainTabTvTodo.setTextColor(Color.parseColor("#2962FF"))
                    deleteSelectAll(true)
                }
            }
            resources.getString(R.string.activity_main_tab_cancel_select_all) -> {
                binding.apply {
                    activityMainTabIvTodo.isSelected = false
                    activityMainTabTvTodo.setText(R.string.activity_main_tab_select_all)
                    activityMainTabTvTodo.setTextColor(Color.BLACK)
                    deleteSelectAll(false)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!isDelete) menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.action_delete -> {
                mainViewModel.title.value?.let {
                    if (itemCount(it) > 0) mainViewModel.enterDeleteMode()
                    else Toast.makeText(get(), "无可删除的对象", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true
    }

    private fun initTags() {
        allTag = get {
            parametersOf(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_note_grey,
                    theme
                ), "全部笔记", 0
            )
        }
        starTag = get {
            parametersOf(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_star_grey,
                    theme
                ), "收藏", 0
            )
        }
        eBookTag = get {
            parametersOf(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_tag_orange,
                    theme
                ), Tag.TAG_E_BOOK, 0
            )
        }
        travelTag = get {
            parametersOf(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_tag_yellow,
                    theme
                ), Tag.TAG_TRAVEL, 0
            )
        }
        personalTag = get {
            parametersOf(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_tag_blue,
                    theme
                ), Tag.TAG_PERSONAL, 0
            )
        }
        lifeTag = get {
            parametersOf(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_tag_green,
                    theme
                ), Tag.TAG_LIFE, 0
            )
        }
        workTag = get {
            parametersOf(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_tag_red, theme),
                Tag.TAG_WORK,
                0
            )
        }
        nullTag = get {
            parametersOf(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_tag_empty,
                    theme
                ), Tag.TAG_NULL, 0
            )
        }
        tags = listOf(eBookTag, travelTag, personalTag, lifeTag, workTag, nullTag)
        tagsBinding = PopWindowTagBinding.inflate(LayoutInflater.from(this))
        tagsBinding.popWindowTagHead.popWindowTagAll.tag = allTag
        tagsBinding.popWindowTagHead.popWindowTagStar.tag = starTag
        tagsBinding.popWindowTagHead.popWindowTagAll.root.setOnClickListener {
            mainViewModel.selectTag(allTag.tag)
            mainViewModel.sumNum(allTag.count)
        }
        tagsBinding.popWindowTagHead.popWindowTagStar.root.setOnClickListener {
            mainViewModel.selectTag(starTag.tag)
            mainViewModel.sumNum(starTag.count)
        }
        tagsBinding.popWindowTagHead.popWindowTagStar.recycleItemTagLine.visibility = View.GONE
        popTag = PopupWindow(
            tagsBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isFocusable = true
            isOutsideTouchable = true
            setOnDismissListener {
                if (isRotate) {
                    binding.activityMainIncludeToolbar.toolbarIvDown.startAnimation(backRotateAnim)
                    isRotate = !isRotate
                }
            }
        }
        tagsAdapter = get { parametersOf(mainViewModel) }
        tagsBinding.popWindowTagRv.adapter = tagsAdapter
        tagsAdapter.submitList(tags)
    }

    private fun showTags() {
        popTag.showAsDropDown(binding.activityMainIncludeToolbar.root)
    }

    private fun deleteSelectAll(selectAll: Boolean) {
        mainViewModel.selectAll(selectAll)
    }

    private fun deleteConfirm() {
        val s =
            if (binding.activityMainTabTvTodo.text.toString() == resources.getString(R.string.activity_main_tab_cancel_select_all)) {
                "是否删除全部笔记？"
            } else {
                "是否删除${deleteCount}条笔记？"
            }
        AlertDialog.Builder(this).apply {
            setMessage(s)
            setPositiveButton("删除") { _, _ ->
                delete()
                mainViewModel.exitDeleteMode()
            }
            setNegativeButton("取消") { _, _ ->
                Toast.makeText(get(), "操作取消", Toast.LENGTH_SHORT).show()
            }
        }.show().apply {
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
    }

    private fun delete() {
        CoroutineScope(Dispatchers.Main).launch {
            noteViewModel.deleteNotesByTime(mainViewModel.deleteSet)
            mainViewModel.deleteSet.clear()
            Toast.makeText(get(), "删除成功", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteSelectTitle(count: Int) {
        if (count == 0) {
            binding.activityMainIncludeToolbar.toolbarTvDeleteTitle.text = "未选择"
        } else {
            val s = "已选择${count}项"
            binding.activityMainIncludeToolbar.toolbarTvDeleteTitle.text = s
        }
    }

    private fun itemCount(tag: String): Int {
        when (tag) {
            allTag.tag -> return allTag.count
            starTag.tag -> return starTag.count
            else -> {
                tags.forEach {
                    if (it.tag == tag) return it.count
                }
            }
        }
        return 0
    }

    companion object {
        const val TAB_NOTE = 0
        const val TAB_DO = 1
    }
}