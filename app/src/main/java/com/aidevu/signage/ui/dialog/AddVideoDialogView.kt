package com.aidevu.signage.ui.dialog

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import com.aidevu.signage.R
import com.aidevu.signage.databinding.DialogAddVideoBinding
import com.aidevu.signage.interfaces.DialogValueOnClickListener
import com.aidevu.signage.ui.base.BaseDialog
import com.aidevu.signage.utils.Constant
import com.aidevu.signage.utils.Log
import com.aidevu.signage.utils.Utils
import com.bumptech.glide.Glide
import java.io.File

class AddVideoDialogView(
    activity: Activity,
    dialogOnClickListener: DialogValueOnClickListener,
    type: Int,
    position: Int,
    editType: String
) :
    BaseDialog<DialogAddVideoBinding>(activity, false) {

    private val dialogOnClickListener: DialogValueOnClickListener
    private val activity: Activity
    private val position: Int
    private val editType: String

    override fun createViewBinding(layoutInflater: LayoutInflater): DialogAddVideoBinding {
        return DialogAddVideoBinding.inflate(layoutInflater)
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

        binding.addVideo.tvOrderEditText1.movementMethod = ScrollingMovementMethod()

        binding.tvApply.setOnClickListener(View.OnClickListener {
            Log.e("값들어갔는지 확인해야한다. Toast 띄워줘야함 4.");
            if(isConfirm()) {
                if(editType == Constant.add) {
                    Utils.showToast(activity, activity.getString(R.string.str_video_add_complete))
                }else{
                    Utils.showToast(activity, activity.getString(R.string.str_video_add_edit_complete))
                }
                dialogOnClickListener.onClick(binding.addVideo.tvOrderEditText.text.toString(),binding.addVideo.tvOrderEditText1.text.toString(), "")
                dismiss()
            }
        })
        binding.tvCancel.setOnClickListener(View.OnClickListener {
            dialogOnClickListener.onCancel()
            dismiss()
        })

        binding.addVideo.deletePath.setOnClickListener {
            binding.addVideo.tvOrderEditText1.text = ""
            Glide
                .with(activity)
                .asBitmap()
                .load("")
                .into(binding.addVideo.imgThumb);
        }
        binding.addVideo.btnSearch.setOnClickListener {
            dialogOnClickListener.onSearch(Constant.video)
        }
    }

    private fun isConfirm(): Boolean {
        if(binding.addVideo.tvOrderEditText.text.toString() == ""){
            Utils.showToast(activity, activity.getString(R.string.str_name_input_hint))
            return false
        }

        if(binding.addVideo.tvOrderEditText1.text.toString() == ""){
            Utils.showToast(activity, activity.getString(R.string.str_image_path_input))
            return false
        }
        return true
    }


    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setFileName(fileName: String) {
        binding.addVideo.tvOrderEditText.setText(fileName)
    }

    fun setFilePath(filePath: String) {
        binding.addVideo.tvOrderEditText1.text = filePath
        Glide
            .with(activity)
            .asBitmap()
            .load(Uri.fromFile(File(filePath)))
            .into(binding.addVideo.imgThumb);
    }

    fun setButtonNameConfirm(btnName: String) {
        binding.tvApply.text = btnName
        binding.tvOneButtonApply.text = btnName
    }

    fun setButtonNameCancel(btnName: String) {
        binding.tvCancel.text = btnName
    }

    fun setPath(path: String, uri: Uri) {
        binding.addVideo.tvOrderEditText1.text = path
        Glide
            .with(activity)
            .asBitmap()
            .load(uri)
            .into(binding.addVideo.imgThumb);
    }

    companion object {
        const val TYPE_ONE_BUTTON = 1
        const val TYPE_TWO_BUTTON = 2
        var TYPE_BUTTON = TYPE_ONE_BUTTON
    }
}