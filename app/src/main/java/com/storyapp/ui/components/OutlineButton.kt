package com.storyapp.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.storyapp.R

class OutlineButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatButton(context, attrs) {

    private var txtColor: Int = 0

    init {
        txtColor = ContextCompat.getColor(context, R.color.dark_blue)
        background = ContextCompat.getDrawable(context, R.drawable.bg_button_outline)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setTextColor(txtColor)
        gravity = Gravity.CENTER
    }
}