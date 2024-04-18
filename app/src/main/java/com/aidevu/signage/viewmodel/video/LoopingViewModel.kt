package com.aidevu.signage.viewmodel.video

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.repository.AdRepository
import com.aidevu.signage.repository.Repository
import com.aidevu.signage.ui.base.BaseViewModel
import com.aidevu.signage.ui.common.ErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class LoopingViewModel @Inject constructor(private val repository: Repository, private val adRepository: AdRepository) : BaseViewModel() {

    private val list = MutableLiveData<List<String>>()
    private val disposable = CompositeDisposable()

    fun getList(actionId: String, screenId: String, datas: String) {
        disposable.add(repository.getList(actionId, screenId, datas)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result -> list.setValue(listOf(result)) }
            ) { error ->
                getErrorState().setValue(
                    ErrorMessage(
                        "getList : " + error.message, System.currentTimeMillis()
                    )
                )
            }
        )
    }

    suspend fun getOrderDescList(): List<Ad> {
        var result = viewModelScope.async {
            adRepository.getOrderDescList()
        }
        return result.await()
    }

    fun getList(): MutableLiveData<List<String>> {
        return list
    }
}