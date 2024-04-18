package com.aidevu.signage.ui.base

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.viewbinding.ViewBinding
import com.aidevu.signage.Apps

abstract class BaseDialog<BINDING : ViewBinding> : Dialog {

    private val disposeTimeOut = 10000L
    var dialogHandler = Handler(Looper.getMainLooper())
    private var _binding: BINDING? = null
    protected val binding get() = _binding!!
    protected abstract fun createViewBinding(layoutInflater: LayoutInflater): BINDING
    private var timeout = false

    protected constructor(context: Context, timeout: Boolean) : super(context) {
        init(context, timeout)
        setOption()
        //hideSystemBar()
    }

    protected constructor(context: Context, timeout: Boolean, cancelable: Boolean, cancelListener: DialogInterface.OnCancelListener?) : super(context, cancelable, cancelListener) {
        init(context, timeout)
        setOption()
        //hideSystemBar()
    }

    protected constructor(context: Context, timeout: Boolean, themeResId: Int) : super(context, themeResId) {
        init(context, timeout)
        setOption()
        //hideSystemBar()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    private fun init(context: Context, timeout: Boolean) {
        _binding = createViewBinding(LayoutInflater.from(context))
        setContentView(binding.root)
        this.timeout = timeout
    }

    private fun setOption() {
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun hideSystemBar() {
        window?.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        )
        if (Build.VERSION.SDK_INT >= 19) {
            window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        } else {
            window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                hideKeyboard(Apps.getApplicationContext())
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hideKeyboard(context: Context) {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            inputManager.hideSoftInputFromWindow(currentFocus?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun show() {
        super.show()
        window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        if(timeout) {
            dialogHandler.postDelayed({
                try {
                    if (this.isShowing) {
                        hide()
                    }
                } catch (e: Exception) {
                }
            }, disposeTimeOut)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        dismiss()
    }

    override fun hide() {
        if (this != null && this.isShowing) {
            super.hide()
            if(timeout) {
                dialogHandler.removeCallbacksAndMessages(null)
            }
            _binding = null
        }
    }
}