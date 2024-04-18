package com.aidevu.signage.viewmodel.settings

import androidx.lifecycle.MutableLiveData
import com.aidevu.signage.repository.Repository
import com.aidevu.signage.ui.base.BaseViewModel
import com.aidevu.signage.ui.common.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class SettingsAdAddImageViewModel @Inject constructor(private val repository: Repository) : BaseViewModel() {

    private val list = MutableLiveData<String>()
    private val disposable = CompositeDisposable()

    fun getList(actionId: String, screenId: String, datas: String) {
        disposable.add(repository.getList(actionId, screenId, datas)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> list.setValue(result) }
            ) { error ->
                getErrorState().setValue(
                    ErrorMessage(
                        "getList : " + error.message, System.currentTimeMillis()
                    )
                )
            }
        )
    }

    fun getList(): MutableLiveData<String> {
        return list
    }
}