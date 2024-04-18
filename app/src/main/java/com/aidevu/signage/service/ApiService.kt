package com.aidevu.signage.service

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("/jsonAction.do?")
    @FormUrlEncoded
    fun getList(@Query("actionId") actionId: String, @Query("screenId") screenId: String, @Field("datas") datas: String): Observable<String>
}