package com.example.memo.view.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.style.*
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.memo.App
import com.example.memo.BaseActivity
import com.example.memo.R
import com.example.memo.databinding.ActivityNoteEditBinding
import com.example.memo.model.bean.*
import com.example.memo.view.fragment.TextEditSelectFragment
import com.example.memo.viewmodel.NoteViwModel
import com.example.memo.viewmodel.TextEditSelectViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class
NoteEditActivity : BaseActivity() {

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
    private lateinit var noteEditHelper: NoteEditHelper
    private var isLoad: Boolean = false
    private val styles = mutableListOf<CharacterStyle>()
    private lateinit var note: Note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        makeStatusBarIconDark()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_edit)

        initView()
        subscribe()
    }

    override fun onBackPressed() {

        super.onBackPressed()
        save()
    }

    private fun initView() {
        Log.d("TAG_12", "init styles size: ${styles.size}")
        supportFragmentManager.beginTransaction().replace(
            R.id.activity_note_edit_select_pop_fragment_container,
            TextEditSelectFragment()
        ).commitNow()
        binding.activityNoteEditIvBack.setOnClickListener {
            onBackPressed()
        }
        binding.activityNoteEditIvSave.setOnClickListener {
            binding.apply {
                activityNoteEditEtTitle.clearFocus()
                activityNoteEditContent.clearFocus()
            }
            if (note.title == "") {
                note.title = binding.activityNoteEditContent.text.toString()
            }
        }
        binding.activityNoteEditIvRedo.setOnClickListener {
//            noteEditHelper.redo(binding.activityNoteEditContent.text)
        }
        binding.activityNoteEditIvUndo.setOnClickListener {
//            noteEditHelper.undo(binding.activityNoteEditContent.text)
        }
        binding.activityNoteEditIvImg.setOnClickListener {
            TODO()
        }
        binding.activityNoteEditIvStar.setOnClickListener {
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
                note.star = isSelected
            }
        }
        binding.activityNoteEditIvText.setOnClickListener {
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
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.d("TAG_06_1", "onTextChanged: $s $start $before $count")
                    mStart = start
                    mEnd = start + count
                }

                override fun afterTextChanged(s: Editable?) {
                    Log.d("TAG_06_2", "afterTextChanged: $s")
                    s?.getSpans(0, s.length, CharacterStyle::class.java)?.forEach {
                        Log.d("TAG_13", "$mStart afterTextChanged: ${it.autoToString()}")
                    }
                    s?.apply {
                        Log.d("TAG_12", "write styles size: ${styles.size} isLoad: $isLoad")
                        styles.clear()
                        styles.apply {
                            add(ForegroundColorSpan(mTextColor))
                            add(RelativeSizeSpan(mTextSize))
                            if (mIsBold)add(StyleSpan(Typeface.BOLD))
                            if (mIsItalic)add(StyleSpan(Typeface.ITALIC))
                            if (mIsUnderline)add(UnderlineSpan())
                        }
                        if (mEnd - mStart > 0) noteEditHelper.write(
                            s,
                            Location(mStart, mEnd),
                            styles,
                            isLoad
                        )
                        if (isLoad) isLoad = false
                    }
                }
            })
        }

        noteViewModel.getNoteByTime(intent.getLongExtra("createTime", System.currentTimeMillis()))
            .observe(this) {
                note = if (it.isNotEmpty()) {
                    it[0].apply {
                        val noteContent = content.generateNoteContent()
                        content = noteContent.content
                        noteEditHelper = NoteEditHelper(noteContent.styles.generateStyles())
                        Log.d("TAG_08", content)
                        isLoad = true
                        binding.activityNoteEditContent.setText(content)
                    }
                } else {
                    noteEditHelper = NoteEditHelper(mutableMapOf())
                    Note(
                        System.currentTimeMillis(),
                        "",
                        "",
                        System.currentTimeMillis(),
                        img = false,
                        star = false
                    )
                }
                binding.note = note
//                noteEditHelperSubscribe()
            }
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
                Log.d("TAG_12", "textSize : $it")
                mTextSize = it
            }
            isBold.observe(this@NoteEditActivity) {
                Log.d("TAG_12", "isBold : $it")
                mIsBold = it
            }
            isItalic.observe(this@NoteEditActivity) {
                Log.d("TAG_12", "isItalic : $it")
                mIsItalic = it
            }
            isUnderLine.observe(this@NoteEditActivity) {
                Log.d("TAG_12", "isUnderLine : $it")
                mIsUnderline = it
            }
        }
    }

//    private fun noteEditHelperSubscribe() {
//        noteEditHelper.isRedo.observe(this) {
//            binding.activityNoteEditIvRedo.apply {
//                if (it) {
//                    isSelected = true
//                    isClickable = true
//                } else {
//                    isSelected = false
//                    isClickable = false
//                }
//            }
//        }
//        noteEditHelper.isUndo.observe(this) {
//            binding.activityNoteEditIvUndo.apply {
//                if (it) {
//                    isSelected = true
//                    isClickable = true
//                } else {
//                    isSelected = false
//                    isClickable = false
//                }
//            }
//        }
//    }

    private fun showIcons() {
        binding.apply {
            activityNoteEditIvRedo.visibility = View.VISIBLE
            activityNoteEditIvUndo.visibility = View.VISIBLE
            activityNoteEditIvSave.visibility = View.VISIBLE
        }
        textEditSelectViewModel.setSelectShow(false)
    }

    private fun hideIcons() {
        binding.apply {
            activityNoteEditIvRedo.visibility = View.GONE
            activityNoteEditIvUndo.visibility = View.GONE
            activityNoteEditIvSave.visibility = View.GONE
        }
    }

    private fun save() {
        CoroutineScope(Dispatchers.IO).launch {
            val m = mutableMapOf<Location, List<String>>()
            Log.d("TAG_15", "${noteEditHelper.operation}")
            noteEditHelper.operation.apply {
                keys.forEach { l ->
                    get(l)?.let { list ->
                        m[l] = list.map {
                            it.autoToString()
                        }
                    }
                }
            }
            note.content = NoteContent(note.content, m).generateString()
            Log.d("TAG_15", "save: ${note.content}")
            noteViewModel.insertNote(note)
        }
    }
}