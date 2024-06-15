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
    private val cancelable: Boolean
) : Dialog(context, R.style.AlertDialogRounded) {

    private lateinit var binding: DialogInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            tvTitle.text = title
            tvDescription.text = message
            btnClose.setOnClickListener {
                dismiss()
            }
        }

        setCancelable(cancelable)
        setCanceledOnTouchOutside(cancelable)
    }
}