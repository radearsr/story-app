package com.storyapp.ui.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.storyapp.R
import com.storyapp.databinding.DialogConfirmationBinding

class DialogConfirmation(
    context: Context, private val title: String,
    private val message: String,
    private val cancelable: Boolean,
    private val txtButtonPositive: String,
    private val txtButtonNegative: String
) : Dialog(context, R.style.AlertDialogRounded) {
    private lateinit var binding: DialogConfirmationBinding

    private lateinit var onButtonClickCallback: OnButtonClickCallback

    fun setOnButtonClickCallback(onButtonClickCallback: OnButtonClickCallback) {
        this.onButtonClickCallback = onButtonClickCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            tvTitle.text = title
            tvDescription.text = message
            btnPositive.text = txtButtonPositive
            btnNegative.text = txtButtonNegative
            btnNegative.setOnClickListener {
                onButtonClickCallback.onButtonNegative(this@DialogConfirmation)
            }
            btnPositive.setOnClickListener {
                onButtonClickCallback.onButtonPositive(this@DialogConfirmation)
            }
        }

        setCancelable(cancelable)
        setCanceledOnTouchOutside(cancelable)
    }

    interface OnButtonClickCallback {
        fun onButtonPositive(dialog: Dialog)
        fun onButtonNegative(dialog: Dialog)
    }
}