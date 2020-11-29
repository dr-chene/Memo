package com.example.memo.view.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.style.*
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.memo.App
import com.example.memo.BaseActivity
import com.example.memo.R
import com.example.memo.databinding.ActivityNoteEditBinding
import com.example.memo.databinding.PopWindowImgSelectBinding
import com.example.memo.databinding.PopWindowTagLiteBinding
import com.example.memo.model.bean.*
import com.example.memo.view.adapter.TagLiteClick
import com.example.memo.view.adapter.TagLiteRecyclerViewAdapter
import com.example.memo.view.fragment.TextEditSelectFragment
import com.example.memo.viewmodel.NoteViwModel
import com.example.memo.viewmodel.TextEditSelectViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

/**
text edit select hide
 * */

class NoteEditActivity : BaseActivity() {

    private lateinit var binding: ActivityNoteEditBinding
    private val noteViewModel: NoteViwModel by viewModel()
    private val textEditSelectViewModel: TextEditSelectViewModel by viewModel()

    private val titleEdit: MutableLiveData<Boolean> = MutableLiveData(false)
    private val contentEdit: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isEdit: MutableLiveData<Boolean> = MutableLiveData(false)

    private var mTextColor = Color.BLACK
    private var mTextSize = 1f
    private var mIsUnderline = false
    private var mIsBold = false
    private var mIsItalic = false
    private var noteEditHelper: NoteEditHelper? = null
    private var isLoad: Boolean = false
    private val styles = mutableListOf<CharacterStyle>()
    private lateinit var curNote: Note
    private lateinit var preNote: Note
    private var first = true
    private lateinit var popTagLites: PopupWindow
    private lateinit var liteTags: List<TagLite>
    private lateinit var tagsAdapter: TagLiteRecyclerViewAdapter
    private lateinit var tagLitesBinding: PopWindowTagLiteBinding
    private lateinit var imgUri: Uri
    private var isNotResult = true
    private val imgSelectBinding by lazy {
        PopWindowImgSelectBinding.inflate(LayoutInflater.from(this))
    }
    private val imgSelectPopWindow by lazy {
        PopupWindow(
            imgSelectBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            true
        ).apply {
            imgSelectBinding.let {
                it.popPhoto.setOnClickListener {
                    imgFromAlbum()
                    dismiss()
                }
                it.popCamera.setOnClickListener {
                    imgFromCamera()
                    dismiss()
                }
                it.popBack.setOnClickListener {
                    dismiss()
                    Toast.makeText(get(), "操作取消", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.makeStatusBarTransparent()
        super.makeStatusBarIconDark()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_edit)

        initView()
        subscribe()
    }

    override fun onBackPressed() {
        if (preNote != curNote) {
            modifyNotice()
        } else super.onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        isNotResult = false
        when (requestCode) {
            CAMERA -> {
                if (resultCode == RESULT_OK) {
                    binding.activityNoteEditContent.apply {
                        insertImg(imgUri, width)
                    }
                } else {
                    Toast.makeText(get(), "未获取到图片", Toast.LENGTH_SHORT).show()
                }
            }
            ALBUM -> {
                if (data != null) {
                    binding.activityNoteEditContent.apply {
                        data.data?.let {
                            insertImg(it, width)
                        }
                    }
                } else {
                    Toast.makeText(get(), "未获取到图片", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openAlbum()
            } else {
                Toast.makeText(get(), "权限获取失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initView() {
        supportFragmentManager.beginTransaction().replace(
            R.id.activity_note_edit_select_pop_fragment_container,
            TextEditSelectFragment()
        ).commitNow()
        initTags()
        binding.activityNoteEditTabDelete.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setMessage("是否删除当前笔记？")
                setPositiveButton("删除") { _, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        noteViewModel.deleteNoteByTime(curNote.createTime)
                        super.onBackPressed()
                    }
                }
                setNegativeButton("取消", null)
            }.show().apply {
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
            }
        }
        binding.activityNoteEditIvBack.setOnClickListener {
            onBackPressed()
        }
        binding.activityNoteEditIvSave.setOnClickListener {
            super.hideInput()
            binding.apply {
                activityNoteEditEtTitle.clearFocus()
                activityNoteEditContent.clearFocus()
            }
            if (curNote.title == "") {
                curNote.title = curNote.content.trim().let {
                    it.substringBefore("<img src=\"") + it.substringAfterLast("\" />")
                }
                binding.activityNoteEditEtTitle.setText(curNote.title)
            }
            noteEditHelper?.clear()
            save()
        }
        binding.activityNoteEditIvRedo.setOnClickListener {
            noteEditHelper?.redo(binding.activityNoteEditContent.text)
        }
        binding.activityNoteEditIvUndo.setOnClickListener {
            noteEditHelper?.undo(binding.activityNoteEditContent.text)
        }
        binding.activityNoteEditTabImg.setOnClickListener {
            if (binding.activityNoteEditContent.hasFocus()) {
                imgSelect()
            } else {
                Toast.makeText(get(), "请选择正确的插入位置", Toast.LENGTH_SHORT).show()
            }
        }
        binding.setTags {
            popTagLites.showAsDropDown(binding.activityNoteEditIvTag)
        }
        binding.activityNoteEditTabStar.setOnClickListener {
            binding.activityNoteEditIvStar.apply {
                if (isSelected) {
                    isSelected = false
                    binding.activityNoteEditTvTabStar.text = "收藏"
                    binding.activityNoteEditTvTabStar.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.black,
                            theme
                        )
                    )
                } else {
                    isSelected = true
                    binding.activityNoteEditTvTabStar.text = "取消收藏"
                    binding.activityNoteEditTvTabStar.setTextColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.blue,
                            theme
                        )
                    )
                }
                curNote.star = isSelected
            }
        }
        binding.activityNoteEditTabText.setOnClickListener {
            super.hideInput()
            textEditSelectViewModel.setSelectShow(true)
        }

        binding.activityNoteEditEtTitle.apply {
            onFocusChangeListener = View.OnFocusChangeListener { _, h ->
                titleEdit.postValue(h)
            }
            addTextChangedListener {
                if (lineCount > 1) {
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                } else {
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
                }
                if (lineCount > 3) {
                    setLines(3)
                    Toast.makeText(App.context, "输出达到上限", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.activityNoteEditContent.apply {
            onFocusChangeListener = View.OnFocusChangeListener { _, h ->
                contentEdit.postValue(h)
            }

            addTextChangedListener(object : TextWatcher {
                var mStart: Int = 0
                var mEnd: Int = 0
                var mBefore: Int = 0
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    mStart = start
                    mEnd = start + count
                    mBefore = before
                }

                override fun afterTextChanged(s: Editable?) {
                    if (isNotResult) {
                        s?.apply {
                            styles.clear()
                            styles.apply {
                                add(ForegroundColorSpan(mTextColor))
                                add(RelativeSizeSpan(mTextSize))
                                if (mIsBold) add(StyleSpan(Typeface.BOLD))
                                if (mIsItalic) add(StyleSpan(Typeface.ITALIC))
                                if (mIsUnderline) add(UnderlineSpan())
                            }
                            noteEditHelper?.write(
                                s,
                                Location(mStart, mEnd),
                                styles,
                                isLoad,
                                mBefore
                            )
                            if (isLoad) isLoad = false
                        }
                    } else {
                        isNotResult = true
                    }
                }
            })
        }

        noteViewModel.getNoteByTime(intent.getLongExtra("createTime", System.currentTimeMillis()))
            .observe(this) {
                if (first) {
                    first = false
                    curNote = if (it.isNotEmpty()) {
                        it[0].apply {
                            val noteStyles = styles.toNoteStyles()
                            val noteImages = imgs.toNoteImages()
                            noteEditHelper =
                                NoteEditHelper(
                                    noteStyles.styles.toStyles(),
                                    noteImages.images.toMutableMap()
                                )
                            isLoad = true
                            liteTags.forEach { tagLite ->
                                if (tagLite.tag == tag) {
                                    binding.activityNoteEditIvTag.setImageDrawable(tagLite.drawable)
                                }
                            }
                            binding.activityNoteEditContent.setText(content)
                            noteEditHelper?.loadImgs(
                                this@NoteEditActivity,
                                binding.activityNoteEditContent.text,
                                binding.activityNoteEditContent.width
                            )
                        }
                    } else {
                        noteEditHelper = NoteEditHelper(mutableMapOf(), mutableMapOf())
                        Note(
                            System.currentTimeMillis(),
                            "",
                            "",
                            "",
                            System.currentTimeMillis(),
                            "",
                            img = false,
                            star = false
                        )
                    }
                    binding.note = curNote
                    if (curNote.star) binding.activityNoteEditIvStar.callOnClick()
                    preNote = curNote.copy()
                    noteEditHelperSubscribe()
                }
            }
    }


    private fun initTags() {
        liteTags = listOf(
            TagLite(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_tag_orange, theme),
                Tag.TAG_E_BOOK
            ),
            TagLite(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_tag_yellow, theme),
                Tag.TAG_TRAVEL
            ),
            TagLite(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_tag_blue, theme),
                Tag.TAG_PERSONAL
            ),
            TagLite(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_tag_green, theme),
                Tag.TAG_LIFE
            ),
            TagLite(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_tag_red, theme),
                Tag.TAG_WORK
            ),
            TagLite(
                ResourcesCompat.getDrawable(resources, R.drawable.ic_tag_empty, theme),
                Tag.TAG_NULL
            )
        )
        tagLitesBinding = PopWindowTagLiteBinding.inflate(LayoutInflater.from(this))
        popTagLites = PopupWindow(
            tagLitesBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            isFocusable = true
            isOutsideTouchable = true
            width = resources.getDimension(R.dimen.pop_window_lite_tag_width).toInt()
        }
        tagsAdapter = TagLiteRecyclerViewAdapter(object : TagLiteClick {
            override fun onClick(tag: TagLite) {
                binding.apply {
                    activityNoteEditIvTag.setImageDrawable(tag.drawable)
                    activityNoteEditTvTag.text = tag.tag
                    popTagLites.dismiss()
                }
            }
        })
        tagLitesBinding.popWindowLiteTagRv.adapter = tagsAdapter
        tagsAdapter.submitList(liteTags)
    }

    private fun subscribe() {
        titleEdit.observe(this) { title ->
            contentEdit.observe(this) { content ->
                isEdit.postValue(title or content)
            }
        }
        isEdit.observe(this) {
            if (it) showIcons() else hideIcons()
        }
        textEditSelectViewModel.selectShow.observe(this) {
            binding.activityNoteEditSelectPopFragmentContainer.visibility =
                if (it) View.VISIBLE else View.GONE
        }
        textEditSelectViewModel.apply {
            textColor.observe(this@NoteEditActivity) {
                mTextColor = it
            }
            textSize.observe(this@NoteEditActivity) {
                mTextSize = it
            }
            isBold.observe(this@NoteEditActivity) {
                mIsBold = it
            }
            isItalic.observe(this@NoteEditActivity) {
                mIsItalic = it
            }
            isUnderLine.observe(this@NoteEditActivity) {
                mIsUnderline = it
            }
            //设置对齐方式：无效果
            align.observe(this@NoteEditActivity) {
                binding.activityNoteEditContent.gravity =
                    binding.activityNoteEditContent.gravity or it
            }
        }
    }

    private fun noteEditHelperSubscribe() {
        noteEditHelper?.isRedo?.observe(this) {
            binding.activityNoteEditIvRedo.apply {
                if (it) {
                    isSelected = true
                    isClickable = true
                } else {
                    isSelected = false
                    isClickable = false
                }
            }
        }
        noteEditHelper?.isUndo?.observe(this) {
            binding.activityNoteEditIvUndo.apply {
                if (it) {
                    isSelected = true
                    isClickable = true
                } else {
                    isSelected = false
                    isClickable = false
                }
            }
        }
    }

    private fun showIcons() {
        binding.apply {
            activityNoteEditIvRedo.visibility = View.VISIBLE
            activityNoteEditIvUndo.visibility = View.VISIBLE
            activityNoteEditIvSave.visibility = View.VISIBLE
        }
    }

    private fun hideIcons() {
        binding.apply {
            activityNoteEditIvRedo.visibility = View.GONE
            activityNoteEditIvUndo.visibility = View.GONE
            activityNoteEditIvSave.visibility = View.GONE
        }
    }

    private fun modifyNotice() {
        AlertDialog.Builder(this).apply {
            setMessage("是否保存更改？")
            setPositiveButton("保存") { _, _ ->
                save()
                super.onBackPressed()
            }
            setNegativeButton("不保存") { _, _ ->
                Toast.makeText(get(), "更改未保存", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
            }
        }.show()
    }

    private fun save() {
        CoroutineScope(Dispatchers.Main).launch {
            val m = mutableMapOf<Location, List<String>>()
            noteEditHelper?.operation?.let {
                it.keys.forEach { l ->
                    it[l]?.let { list ->
                        m[l] = list.map { style ->
                            style.autoToString()
                        }
                    }
                }
            }
            val images = noteEditHelper?.imgs?.toMutableMap() ?: mutableMapOf()
            val delete = mutableMapOf<Int, String>()
            images.keys.forEach {
                images[it]?.let { str ->
                    if (!binding.activityNoteEditContent.text.toString()
                            .contains("<img src=\"${str}\" />")
                    ) {
                        delete[it] = str
                    }
                }
            }
            delete.keys.forEach {
                images.remove(it)
            }
            curNote.styles = NoteStyles(m).generateString()
            curNote.changeTime = System.currentTimeMillis()
            curNote.imgs = NoteImages(images).generateString()
            curNote.img = images.isNotEmpty()
            if (curNote.title == "") curNote.title = curNote.content
            noteViewModel.insertNote(curNote)
            preNote = curNote.copy()
            Toast.makeText(get(), "保存成功", Toast.LENGTH_SHORT).show()
        }
    }

    private fun imgSelect() {
        imgSelectPopWindow.showAsDropDown(binding.root)
    }

    private fun imgFromCamera() {
        val imgName = "output_img_${System.currentTimeMillis()}.jpg"
        val imgFile = File(externalCacheDir, imgName).apply {
            if (exists()) delete()
            createNewFile()
        }
        imgUri = FileProvider.getUriForFile(this, "com.example.memo.fileprovider", imgFile)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        }
        startActivityForResult(intent, CAMERA)
    }

    private fun imgFromAlbum() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )
        } else {
            openAlbum()
        }
    }

    private fun openAlbum() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
        }
        startActivityForResult(intent, ALBUM)
    }

    private fun insertImg(uri: Uri, screenWidth: Int) {
        noteEditHelper?.insertImg(
            this,
            uri,
            binding.activityNoteEditContent.text,
            binding.activityNoteEditContent.selectionStart,
            screenWidth,
            false
        )
    }

    companion object {
        private const val CAMERA = 328
        private const val ALBUM = 329
    }
}