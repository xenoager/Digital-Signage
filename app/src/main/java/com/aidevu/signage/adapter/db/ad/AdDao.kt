package com.aidevu.signage.adapter.db.ad

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AdDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ad: Ad)

    @Query("DELETE From Ad WHERE filePath = :filePath AND id = :id")
    suspend fun delete(filePath: String, id: Int)

    @Query("DELETE FROM Ad")
    suspend fun clearTableList()

    @Query("SELECT * FROM Ad ORDER BY 'order' ASC")
    suspend fun getOrderAscList(): List<Ad>

    @Query("SELECT * FROM Ad ORDER BY 'order' DESC")
    suspend fun getOrderDescList(): List<Ad>

    @Query("SELECT * FROM Ad WHERE filePath = :filePath")
    suspend fun getFilePathList(filePath: String): Ad

    @Query("SELECT * FROM Ad WHERE id = :id")
    suspend fun getIdPathList(id: Int): Ad

    @Update
    suspend fun update(ad: Ad): Int

    @Query("SELECT MAX('index') FROM Ad")
    suspend fun getLastIndex(): Int

    @Query("SELECT MAX('order') FROM Ad")
    suspend fun getLastOrder(): Int
}