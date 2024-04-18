package com.aidevu.signage.repository

import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.adapter.db.ad.AdDao
import com.aidevu.signage.utils.Constant
import com.aidevu.signage.utils.Log

import javax.inject.Inject

class AdRepository @Inject constructor(private val adDao: AdDao) {

    suspend fun getOrderAscList(): List<Ad> {
        return adDao.getOrderAscList()
    }

    suspend fun getOrderDescList(): List<Ad> {
        return adDao.getOrderDescList()
    }

    private suspend fun getAd(adTitle: String): Ad {
        return adDao.getFilePathList(adTitle)
    }

    private suspend fun getAd(id: Int): Ad {
        return adDao.getIdPathList(id)
    }

    suspend fun insertAd(ad: Ad) {
        try {
            adDao.insert(ad)
        } catch (e: Exception) {
            Log.d("이미 존재하는 insertAd : " + e.message)
        }
    }

    suspend fun deleteAdName(filePath: String, id: Int) {
        val temp = getAd(filePath)
        if (temp != null && temp.getFilePath().isNotEmpty()) {
            adDao.delete(filePath, id)
        } else {
//            Utils.showToast(Apps.getApplicationContext(), activity.getString(R.string.str_delete_error_duplication))
        }
    }

    suspend fun clearTableList() {
        adDao.clearTableList()
    }

    suspend fun updateData(type: String, id: Int, fileName: String, path: String, duration: Int): Boolean {
        val getAd = getAd(id)
        if (getAd != null && getAd.getFilePath().isEmpty()) {
            //Utils.showToast(activity, activity.getString(R.string.str_rename_error_duplication))
            return false
        }

        val ad = Ad()
        ad.setId(getAd.getId())
        ad.setIndex(getAd.getIndex())
        ad.setFileType(getAd.getFileType())
        ad.setFileName(fileName)
        ad.setFilePath(path)
        if(type == Constant.image){
            ad.setDuration(duration)
        }else{
            ad.setDuration(0)
        }
        ad.setOrder(getAd.getOrder())
        var flag = 0
        try {
            flag = adDao.update(ad)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("update 오류 11")
            //Utils.showToast(activity, activity.getString(R.string.str_rename_fail))
        }
        return if(flag == Constant.DB_UPDATE_FAIL) {
            Log.d("update DB_UPDATE_SUCCESS 33")
           // Utils.showToast(activity, activity.getString(R.string.str_rename_success))
            true
        }else{
            Log.d("update 오류 22")
            //Utils.showToast(activity, activity.getString(R.string.str_rename_fail))
            false
        }
    }

    suspend fun updateData(id: Int, order: Int): Boolean {
        val getAd = getAd(id)
        if (getAd != null && getAd.getFilePath().isEmpty()) {
            //Utils.showToast(activity, activity.getString(R.string.str_rename_error_duplication))
            return false
        }

        val ad = Ad()
        ad.setId(getAd.getId())
        ad.setIndex(getAd.getIndex())
        ad.setFileType(getAd.getFileType())
        ad.setFileName(getAd.getFileName())
        ad.setFilePath(getAd.getFilePath())
        ad.setDuration(getAd.getDuration())
        Log.d("getDuration3 :" + getAd.getDuration())
        ad.setOrder(order)
        var flag = 0
        try {
            flag = adDao.update(ad)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("update 오류 1")
            //Utils.showToast(activity, activity.getString(R.string.str_rename_fail))
        }
        return if(flag == Constant.DB_UPDATE_FAIL) {
            Log.d("update DB_UPDATE_SUCCESS")
            // Utils.showToast(activity, activity.getString(R.string.str_rename_success))
            true
        }else{
            Log.d("update 오류")
            //Utils.showToast(activity, activity.getString(R.string.str_rename_fail))
            false
        }
    }

    suspend fun getLastIndex(): Int {
        return adDao.getLastIndex()
    }

    suspend fun getLastOrder(): Int {
        return adDao.getLastOrder()
    }
}
