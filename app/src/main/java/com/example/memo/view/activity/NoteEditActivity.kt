package com.example.memo.view.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.memo.App
import com.example.memo.BaseActivity
import com.example.memo.R
import com.example.memo.databinding.ActivityNoteEditBinding
import com.example.memo.model.bean.Note
import com.example.memo.viewmodel.TextEditSelectViewModel
import com.example.memo.viewmodel.NoteViwModel
import org.koin.android.viewmodel.ext.android.viewModel

class NoteEditActivity : BaseActivity() {

    private lateinit var binding: ActivityNoteEditBinding
    private val noteViewModel: NoteViwModel by viewModel()
    private val textEditSelectViewModel: TextEditSelectViewModel by viewModel()

    private val titleEdit: MutableLiveData<Boolean> = MutableLiveData(false)
    private val contentEdit: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isEdit: MutableLiveData<Boolean> = MutableLiveData(false)

    private var colorSpan = ForegroundColorSpan(Color.BLACK)
    private var sizeSpan = RelativeSizeSpan(1f)
    private val underlineSpan = UnderlineSpan()
    private val boldSpan = StyleSpan(Typeface.BOLD)
    private val italicSpan = StyleSpan(Typeface.ITALIC)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        makeStatusBarTransparent()
        makeStatusBarIconDark()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_note_edit)

        initView()
        subscribe()
    }

    private fun initView() {
        Log.d("TAG_01", "initView: NoteEditActivity")
        binding.activityNoteEditIvBack.setOnClickListener {
            finish()
        }
        binding.activityNoteEditIvSave.setOnClickListener {
            binding.apply {
                activityNoteEditEtTitle.clearFocus()
                activityNoteEditContent.clearFocus()
            }
        }
        binding.activityNoteEditIvRedo.setOnClickListener {

        }
        binding.activityNoteEditIvUndo.setOnClickListener {

        }
        binding.activityNoteEditIvImg.setOnClickListener {

        }
        binding.activityNoteEditIvStar.setOnClickListener {

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
                    s?.apply {
                        setSpan(colorSpan, mStart, mEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                        setSpan(sizeSpan, mStart, mEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
            })
        }

        noteViewModel.getNoteByTime(intent.getLongExtra("createTime", System.currentTimeMillis()))
            .observe(this) {
                binding.note = it ?: Note(
                    System.currentTimeMillis(),
                    "",
                    "",
                    System.currentTimeMillis(),
                    false
                )
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
            textEditSelectViewModel.setSelectShow(false)
        }
        textEditSelectViewModel.selectShow.observe(this) {
            binding.activityNoteEditSelectPopFragmentContainer.visibility =
                if (it) View.VISIBLE else View.GONE
            if (!it) {
                textEditSelectViewModel.apply {
                    colorSpan = ForegroundColorSpan(textColor)
                    sizeSpan = RelativeSizeSpan(textSize)
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
}