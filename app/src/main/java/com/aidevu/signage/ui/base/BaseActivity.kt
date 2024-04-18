package com.aidevu.signage.ui.base

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.aidevu.signage.Apps
import com.aidevu.signage.interfaces.DialogOnClickListener
import com.aidevu.signage.ui.dialog.AlertDialogView
import com.aidevu.signage.utils.PreferenceUtil

abstract class BaseActivity<BINDING : ViewBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected lateinit var viewModel: VM
    private var _binding: BINDING? = null
    protected val binding get() = _binding!!
    private val disposeTimeOut = 10000L

    lateinit var prefs: PreferenceUtil

    @NonNull
    protected abstract fun createViewModel(): VM

    @NonNull
    protected abstract fun createViewBinding(layoutInflater: LayoutInflater): BINDING

    @NonNull
    protected abstract fun observeData()

    private lateinit var app: Apps
    var dialogHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // hideSystemBar()
        _binding = createViewBinding(LayoutInflater.from(this))
        prefs = PreferenceUtil(this)
        setContentView(binding.root)
        viewModel = createViewModel()
        setFinishOnTouchOutside(false)

        app = application as Apps
    }

    override fun onDestroy() {
        super.onDestroy()
        overridePendingTransition(0, 0)
        _binding = null
    }


    fun hideSystemBar() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        window.decorView.systemUiVisibility = android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        } else {
            window.decorView.systemUiVisibility =
                (View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
    }

    private fun hideKeyboard() {
        val inputManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            currentFocus?.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

    protected open fun showDialog(view: View) {
        hideKeyboard()
        view.visibility = View.VISIBLE
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
        dialogHandler.postDelayed({
            try {
                if (view.visibility === View.VISIBLE) {
                    hideDialog(view)
                }
            } catch (e: Exception) {
            }
        }, disposeTimeOut)
    }

    protected open fun hideDialog(view: View) {
        hideKeyboard()
        dialogHandler.removeCallbacksAndMessages(null)
        view.visibility = View.GONE
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    protected open fun showErrorDialog(view: View, title: String, content: String) {
        val dialogView = AlertDialogView(this, object : DialogOnClickListener {
            override fun onClick(str: String?) {
                hideDialog(view)
            }

            override fun onCancel() {
                hideDialog(view)
            }
        }, AlertDialogView.TYPE_ONE_BUTTON)
        dialogView.setTitle(title)
        dialogView.setContent(content)
        dialogView.show()
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    override fun onResume() {
        super.onResume()
//        hideSystemBar()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> hideKeyboard()
        }
        return super.dispatchTouchEvent(event)
    }
}