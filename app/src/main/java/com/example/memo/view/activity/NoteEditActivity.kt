package com.example.memo.view.activity

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import com.example.memo.App
import com.example.memo.BaseActivity
import com.example.memo.R
import com.example.memo.databinding.ActivityNoteEditBinding
import com.example.memo.model.bean.Note
import com.example.memo.viewmodel.NoteViwModel
import org.koin.android.viewmodel.ext.android.viewModel

class NoteEditActivity : BaseActivity() {

    private lateinit var binding: ActivityNoteEditBinding
    private val noteViewModel: NoteViwModel by viewModel()

    private val titleEdit: MutableLiveData<Boolean> = MutableLiveData(false)
    private val contentEdit: MutableLiveData<Boolean> = MutableLiveData(false)
    private val isEdit: MutableLiveData<Boolean> = MutableLiveData(false)

    private var colorSpan = ForegroundColorSpan(Color.BLACK)
    private var sizeSpan = RelativeSizeSpan(1f)
    private val underlineSpan = UnderlineSpan()
    private val boldSpan = StyleSpan(Typeface.BOLD)
    private val italicSpan = StyleSpan(Typeface.ITALIC)

    private val backClick = View.OnClickListener {
        finish()
    }
    private val saveClick = View.OnClickListener {
            binding.apply {
                activityNoteEditEtTitle.clearFocus()
                activityNoteEditContent.clearFocus()
            }
    }
    private val textEditClick = View.OnClickListener {
        binding.activityNoteEditSelectPopFragmentContainer.visibility = View.VISIBLE
    }

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
        binding.backClick = backClick
        binding.saveClick = saveClick
        binding.textEditClick = textEditClick

        binding.activityNoteEditEtTitle.apply {
            onFocusChangeListener = View.OnFocusChangeListener{ _, h ->
                titleEdit.postValue(h)
            }
            addTextChangedListener {
                if (lineCount > 1) {
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                } else {
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
                }
                if (lineCount > 3){
                    setLines(3)
                    Toast.makeText(App.context, "输出达到上限", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.activityNoteEditContent.apply {
            onFocusChangeListener = View.OnFocusChangeListener{ _, h ->
                contentEdit.postValue(h)
            }
        }

        noteViewModel.getNoteByTime(intent.getLongExtra("createTime", System.currentTimeMillis())).observe(this){
            binding.note = it ?: Note(System.currentTimeMillis(), "", "", System.currentTimeMillis(), false)
        }
    }

    private fun subscribe() {
        titleEdit.observe(this){ title ->
            contentEdit.observe(this){ content ->
                isEdit.postValue(title or content)
            }
        }
        isEdit.observe(this){
            if (it) showIcons() else hideIcons()
        }
    }

    private fun showIcons(){
        binding.apply {
            activityNoteEditIvRedo.visibility = View.VISIBLE
            activityNoteEditIvUndo.visibility = View.VISIBLE
            activityNoteEditIvSave.visibility = View.VISIBLE
        }
    }

    private fun hideIcons(){
        binding.apply {
            activityNoteEditIvRedo.visibility = View.GONE
            activityNoteEditIvUndo.visibility = View.GONE
            activityNoteEditIvSave.visibility = View.GONE
        }
    }
}