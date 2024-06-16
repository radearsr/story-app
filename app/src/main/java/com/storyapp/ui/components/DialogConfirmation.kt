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
    private val txtButtonYes: String,
    private val txtButtonNo: String
) : Dialog(context, R.style.AlertDialogRounded) {
    private lateinit var binding: DialogConfirmationBinding

    private lateinit var onButonClickCallback: OnButtonClickCallback

    fun setOnButtonClickCallback(onButtonClickCallback: OnButtonClickCallback) {
        this.onButonClickCallback = onButtonClickCallback
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogConfirmationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            tvTitle.text = title
            tvDescription.text = message
            btnYes.text = txtButtonYes
            btnNo.text = txtButtonNo
            btnNo.setOnClickListener {
                onButonClickCallback.onButtonNo(this@DialogConfirmation)
            }
            btnYes.setOnClickListener {
                onButonClickCallback.onButtonYes(this@DialogConfirmation)
            }
        }

        setCancelable(cancelable)
        setCanceledOnTouchOutside(cancelable)
    }

    interface OnButtonClickCallback {
        fun onButtonYes(dialog: Dialog)
        fun onButtonNo(dialog: Dialog)
    }
}