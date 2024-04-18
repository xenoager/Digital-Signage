package com.aidevu.signage.viewmodel

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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository, private val adRepository: AdRepository) : BaseViewModel() {


    private val list = MutableLiveData<String>()
    private val adList = MutableLiveData<List<Ad>>()
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

    fun getAdList(ad: Ad) {
        viewModelScope.launch {
            adRepository.insertAd(ad)
            adList.setValue(adRepository.getOrderAscList())
        }
    }

    suspend fun getOrderAscList(): List<Ad> {
        var result = viewModelScope.async {
            adRepository.getOrderAscList()
        }
        return result.await()
    }

    suspend fun getOrderDescList(): List<Ad> {
        var result = viewModelScope.async {
            adRepository.getOrderDescList()
        }
        return result.await()
    }

    suspend fun getLastIndex(): Int {
        var result = viewModelScope.async {
            adRepository.getLastIndex()
        }

        return result.await()
    }

    suspend fun getLastOrder(): Int {
        var result = viewModelScope.async {
            adRepository.getLastOrder()
        }
        return result.await()
    }

    suspend fun clearTableList() {
        viewModelScope.launch {
            adRepository.clearTableList()
        }
    }

    fun getList(): MutableLiveData<String> {
        return list
    }

    fun getAdList(): MutableLiveData<List<Ad>> {
        return adList
    }

}