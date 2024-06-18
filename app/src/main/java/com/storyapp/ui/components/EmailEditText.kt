package com.storyapp.ui.components

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.storyapp.utils.validEmail

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs, defStyleAttr) {

    init {
        textAlignment = View.TEXT_ALIGNMENT_TEXT_START
        inputType = InputType.TYPE_CLASS_TEXT

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                s?.let {
                    validateEmailAddress(it.toString())
                }
            }
        })
    }

    private fun validateEmailAddress(email: String) {
        if (!validEmail(email)) {
            this.error = "Alamat email tidak valid"
        }
    }
}