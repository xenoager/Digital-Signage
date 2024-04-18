package com.aidevu.signage.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.aidevu.signage.databinding.DialogAlertBinding
import com.aidevu.signage.interfaces.DialogOnClickListener
import com.aidevu.signage.ui.base.BaseDialog

class AlertDialogView(
    context: Context,
    dialogOnClickListener: DialogOnClickListener,
    type: Int
) :
    BaseDialog<DialogAlertBinding>(context, true) {
    private val dialogOnClickListener: DialogOnClickListener

    override fun createViewBinding(layoutInflater: LayoutInflater): DialogAlertBinding {
        return DialogAlertBinding.inflate(layoutInflater)
    }

    init {
        this.dialogOnClickListener = dialogOnClickListener
        TYPE_BUTTON = type
        if (TYPE_BUTTON == TYPE_ONE_BUTTON) {
            binding.tvCancel.visibility = View.GONE
            binding.tvApply.visibility = View.GONE
            binding.tvOneButtonApply.visibility = View.VISIBLE
        } else if (TYPE_BUTTON == TYPE_TWO_BUTTON) {
            binding.tvCancel.visibility = View.VISIBLE
            binding.tvApply.visibility = View.VISIBLE
            binding.tvOneButtonApply.visibility = View.GONE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.tvOneButtonApply.setOnClickListener(View.OnClickListener {
            dialogOnClickListener.onClick("")
            dismiss()
        })
        binding.tvApply.setOnClickListener(View.OnClickListener {
            dialogOnClickListener.onClick("")
            dismiss()
        })
        binding.tvCancel.setOnClickListener(View.OnClickListener {
            dialogOnClickListener.onCancel()
            dismiss()
        })
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setContent(content: String) {
        binding.tvBody.text = content
    }

    fun setButtonNameConfirm(btnName: String) {
        binding.tvApply.text = btnName
        binding.tvOneButtonApply.text = btnName
    }

    fun setButtonNameCancel(btnName: String) {
        binding.tvCancel.text = btnName
    }

    companion object {
        const val TYPE_ONE_BUTTON = 1
        const val TYPE_TWO_BUTTON = 2
        var TYPE_BUTTON = TYPE_ONE_BUTTON
    }
}