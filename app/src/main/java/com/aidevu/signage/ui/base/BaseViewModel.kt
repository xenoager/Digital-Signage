package com.aidevu.signage.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aidevu.signage.ui.common.ErrorMessage

open class BaseViewModel: ViewModel() {

    private var errorState: MutableLiveData<ErrorMessage> = MutableLiveData()

    fun getErrorState(): MutableLiveData<ErrorMessage> {
        return errorState
    }
}