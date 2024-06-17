package com.storyapp.ui.components

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.storyapp.R
import com.storyapp.databinding.DialogInformationBinding

class DialogInformation(
    context: Activity,
    private val title: String,
    private val message: String,
    private val txtButtonClose: String,
    private val cancelable: Boolean,
) : Dialog(context, R.style.AlertDialogRounded) {

    private lateinit var binding: DialogInformationBinding

    private lateinit var onButtonClickCallback: OnButtonClickCallback

    fun setOnButtonClickCallback(onButtonClickCallback: OnButtonClickCallback) {
        this.onButtonClickCallback = onButtonClickCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            tvTitle.text = title
            tvDescription.text = message
            btnClose.text = txtButtonClose
            btnClose.setOnClickListener {
                onButtonClickCallback.onButtonClose(this@DialogInformation)
            }
        }

        setCancelable(cancelable)
        setCanceledOnTouchOutside(cancelable)
    }

    interface OnButtonClickCallback {
        fun onButtonClose(dialog: Dialog)
    }
}