package com.aidevu.signage.ui.dialog

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import com.aidevu.signage.R
import com.aidevu.signage.databinding.DialogAddImageBinding
import com.aidevu.signage.interfaces.DialogValueOnClickListener
import com.aidevu.signage.ui.base.BaseDialog
import com.aidevu.signage.utils.Constant
import com.aidevu.signage.utils.Log
import com.aidevu.signage.utils.Utils

class AddImageDialogView(
    activity: Activity,
    dialogOnClickListener: DialogValueOnClickListener,
    type: Int,
    position: Int,
    editType: String
) :
    BaseDialog<DialogAddImageBinding>(activity, false) {

    private val dialogOnClickListener: DialogValueOnClickListener
    private val activity: Activity
    private val position: Int
    private val editType: String

    override fun createViewBinding(layoutInflater: LayoutInflater): DialogAddImageBinding {
        return DialogAddImageBinding.inflate(layoutInflater)
    }

    init {
        this.position = position
        this.activity = activity
        this.editType = editType
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

        binding.addImage.tvOrderEditText1.movementMethod = ScrollingMovementMethod()

        binding.tvApply.setOnClickListener(View.OnClickListener {
            Log.e("값들어갔는지 확인해야한다. Toast 띄워줘야함 2.");
            if(isConfirm()) {
                if(editType == Constant.add) {
                    Utils.showToast(activity, activity.getString(R.string.str_image_add_complete))
                }else{
                    Utils.showToast(activity, activity.getString(R.string.str_image_add_edit_complete))
                }
                dialogOnClickListener.onClick(binding.addImage.tvOrderEditText.text.toString(), binding.addImage.tvOrderEditText1.text.toString(), binding.addImage.tvOrderEditText2.text.toString())
                dismiss()
            }
        })
        binding.tvCancel.setOnClickListener(View.OnClickListener {
            dialogOnClickListener.onCancel()
            dismiss()
        })

        binding.addImage.deletePath.setOnClickListener {
            binding.addImage.tvOrderEditText1.text = ""
            binding.addImage.imgThumb.setImageURI(null)
        }
        binding.addImage.deleteDuration.setOnClickListener {
            binding.addImage.tvOrderEditText2.setText("")
        }
        binding.addImage.btnSearch.setOnClickListener {
            dialogOnClickListener.onSearch(Constant.image)
        }
    }

    private fun isConfirm(): Boolean {
        if(binding.addImage.tvOrderEditText.text.toString() == ""){
            Utils.showToast(activity, activity.getString(R.string.str_name_input_hint))
            return false
        }

        if(binding.addImage.tvOrderEditText1.text.toString() == ""){
            Utils.showToast(activity, activity.getString(R.string.str_image_path_input))
            return false
        }

        if(binding.addImage.tvOrderEditText2.text.toString() == ""){
            Utils.showToast(activity, activity.getString(R.string.str_image_duration_input))
            return false
        }
        return true
    }

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setFileName(fileName: String) {
        binding.addImage.tvOrderEditText.setText(fileName)
    }

    fun setFilePath(filePath: String) {
        binding.addImage.tvOrderEditText1.text = filePath
        binding.addImage.imgThumb.setImageBitmap(Utils.pathToBitmap(filePath))
    }

    fun setFileDuration(duration: String) {
        binding.addImage.tvOrderEditText2.setText(duration)
    }

    fun setButtonNameConfirm(btnName: String) {
        binding.tvApply.text = btnName
        binding.tvOneButtonApply.text = btnName
    }

    fun setButtonNameCancel(btnName: String) {
        binding.tvCancel.text = btnName
    }

    fun setPath(path: String, uri: Uri) {
        binding.addImage.tvOrderEditText1.text = path
        binding.addImage.imgThumb.setImageURI(uri)
    }

    companion object {
        const val TYPE_ONE_BUTTON = 1
        const val TYPE_TWO_BUTTON = 2
        var TYPE_BUTTON = TYPE_ONE_BUTTON
    }
}