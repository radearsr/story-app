package com.storyapp.ui.components

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.storyapp.R
import com.storyapp.utils.validPassword

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    private var isPasswordVisible = false

    private val visibilityToggleDrawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_visibility)?.apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
    }
    private val visibilityOffToggleDrawable by lazy {
        ContextCompat.getDrawable(context, R.drawable.ic_visibility_off)?.apply {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        }
    }

    init {
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        background = ContextCompat.getDrawable(context, R.drawable.edit_text_border)
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, visibilityToggleDrawable, null)

        addTextChangedListener(
            doAfterTextChanged {
                setError(
                    if (!validPassword(it.toString())) {
                        context.getString(R.string.password_required)
                    } else {
                        null
                    }, null
                )
            }
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableRight =
                if (isPasswordVisible) visibilityOffToggleDrawable else visibilityToggleDrawable
            if (event.rawX >= (right - drawableRight?.bounds?.width()!!)) {
                togglePasswordVisibility()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun togglePasswordVisibility() {
        val selectionStart = selectionStart
        val selectionEnd = selectionEnd
        isPasswordVisible = !isPasswordVisible
        inputType = if (isPasswordVisible) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                visibilityOffToggleDrawable,
                null
            )
            InputType.TYPE_CLASS_TEXT
        } else {
            setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                visibilityToggleDrawable,
                null
            )
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        setSelection(selectionStart, selectionEnd)
    }
}