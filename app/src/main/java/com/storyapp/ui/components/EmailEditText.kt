package com.storyapp.ui.components

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doAfterTextChanged
import com.storyapp.R
import com.storyapp.utils.validEmail

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        inputType = InputType.TYPE_CLASS_TEXT

        addTextChangedListener(
            doAfterTextChanged {
                it?.let {
                    validateEmailAddress(it.toString())
                }
            }
        )
    }

    private fun validateEmailAddress(email: String) {
        if (!validEmail(email)) {
            this.error = context.getString(R.string.email_not_valid)
        }
    }
}