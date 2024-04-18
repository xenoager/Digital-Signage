package com.aidevu.signage.repository

import com.aidevu.signage.service.ApiService
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

class Repository @Inject constructor(private val apiService: ApiService) {

    fun getList(sActionId: String, screenId: String, datas: String): Observable<String> {
        return apiService.getList(sActionId, screenId, datas)
    }
}