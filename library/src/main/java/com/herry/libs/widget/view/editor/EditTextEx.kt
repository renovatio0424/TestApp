package com.herry.libs.widget.view.editor

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText
import com.herry.libs.R
import com.herry.libs.data_checker.DataCheckerMandatory
import com.herry.libs.data_checker.DataCheckerMandatoryDelegate
import com.herry.libs.text.StringUtil

/**
 * Created by herry.park
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class EditTextEx : AppCompatEditText, DataCheckerMandatory {
    private var baseText = ""
    private var baseHint: CharSequence = ""
    private var mandatoryTextCount = 0
    private var hideHintOnFocus = true
    private var editable = true

    private val checkerMandatory: DataCheckerMandatoryDelegate = DataCheckerMandatoryDelegate(this)

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditTextEx)
        hideHintOnFocus = typedArray.getBoolean(R.styleable.EditTextEx_etex_focusHintDisable, true)
        mandatoryTextCount = typedArray.getInt(R.styleable.EditTextEx_etex_mandatoryCount, 0)
        editable = typedArray.getBoolean(R.styleable.EditTextEx_etex_editable, true)
        typedArray.recycle()

        baseHint = super.getHint() ?: ""
        baseText = if (super.getText() != null) super.getText().toString() else ""
        this.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                when (event.action) {
                    KeyEvent.ACTION_DOWN -> {
                        true
                    }
                    KeyEvent.ACTION_UP -> {
                        v.clearFocus()
                        true
                    }
                    else -> {
                        false
                    }
                }
            } else {
                false
            }
        }
        this.addTextChangedListener(object : OnEditorTextWatcher(){
            override fun afterTextChanged(s: Editable) {
                checkerMandatory.setMandatory(
                    isMandatoryTextCount(s.toString(), mandatoryTextCount),
                    !StringUtil.equals(baseText, s.toString())
                )
            }
        })
    }

    fun setMandatoryTextCount(mandatoryTextCount: Int) {
        this.mandatoryTextCount = mandatoryTextCount
    }

    fun setHideHintOnFocus(hideHintOnFocus: Boolean) {
        this.hideHintOnFocus = hideHintOnFocus
    }

    fun setBaseText(text: String) {
        baseText = text
        setText(text)
        checkerMandatory.setMandatory(
            mandatory = isMandatoryTextCount(baseText, mandatoryTextCount),
            change = false, notify = true
        )
    }

    fun setBaseHint(hint: CharSequence) {
        baseHint = hint
        setHint(hint)
    }

    // get the current state
    fun isEditable(): Boolean {
        return editable
    }

    // set your desired behaviour
    fun setEditable(editable: Boolean) {
        this.editable = editable
    }

    fun requestFocusAndShowInput() {
        val context = this.context ?: return
        postDelayed({
            this@EditTextEx.isFocusable = true
            this@EditTextEx.isFocusableInTouchMode = true
            this@EditTextEx.requestFocus()
            this@EditTextEx.setSelection(if (null != super.getText()) super.getText()?.length ?: 0 else 0)
            val imm: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.showSoftInput(this@EditTextEx, 0, null)
        }, context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
    }

    fun clearFocusAndHideInput() {
        this.clearFocus()
        val imm: InputMethodManager? = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (hideHintOnFocus && baseHint.isNotEmpty()) {
            if (focused) {
                super.setHint("")
            } else {
                super.setHint(baseHint)
            }
        }
    }

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
            clearFocus()
        }
        return super.onKeyPreIme(keyCode, event)
    }

    private fun isMandatoryTextCount(str: String?, count: Int): Boolean {
        return (str?.length ?: return false) > count
    }

    override fun isChanged(): Boolean = checkerMandatory.isChanged()

    override fun isMandatory(): Boolean = checkerMandatory.isMandatory()

    override fun addOnCheckerListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        checkerMandatory.addOnCheckerListener(listener)
    }

    override fun removeOnCheckerListener(listener: DataCheckerMandatory.OnDataCheckerChangedListener) {
        checkerMandatory.removeOnCheckerListener(listener)
    }

    override fun dispatchTouchEvent(motionEvent: MotionEvent): Boolean {
        if (editable) {
            // default behaviour of an EditText
            super.dispatchTouchEvent(motionEvent)
            return true
        }

        // achieve the click-behaviour of a TextView (it's cuztom)
        (parent as? ViewGroup)?.onTouchEvent(motionEvent)

        return true
    }
}