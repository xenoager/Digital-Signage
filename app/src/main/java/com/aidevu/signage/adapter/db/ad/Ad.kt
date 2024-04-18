package com.aidevu.signage.adapter.db.ad

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Ad")
class Ad {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private var id: Int? = null

    @ColumnInfo(name = "index")
    private var index = 0 // 위치 index

    @ColumnInfo(name = "fileName")
    private lateinit var fileName: String// 파일명

    @ColumnInfo(name = "fileType")
    private lateinit var fileType  : String // image, video

    @ColumnInfo(name = "filePath")
    private lateinit var filePath: String // 파일 경로

    @ColumnInfo(name = "duration")
    private var duration = 0 // 시간

    @ColumnInfo(name = "order")
    private var order = 0 // 순서


    constructor() {}

    constructor(index: Int, fileType: String, fileName: String, filePath: String, duration: Int, order: Int) {
        this.index = index
        this.fileType = fileType
        this.fileName = fileName
        this.filePath = filePath
        this.duration = duration
        this.order = order
    }

    fun getId(): Int? {
        return id
    }

    fun setId(id: Int?) {
        this.id = id
    }

    fun getIndex(): Int {
        return index
    }

    fun setIndex(index: Int) {
        this.index = index
    }

    fun getFileType(): String {
        return fileType
    }

    fun setFileType(fileType: String) {
        this.fileType = fileType
    }

    fun getFilePath(): String {
        return filePath
    }

    fun setFilePath(filePath: String) {
        this.filePath = filePath
    }

    fun getDuration(): Int {
        return duration
    }

    fun setDuration(duration: Int) {
        this.duration = duration
    }

    fun getOrder(): Int {
        return order
    }

    fun setOrder(order: Int) {
        this.order = order
    }

    fun getFileName(): String {
        return fileName
    }

    fun setFileName(fileName: String) {
        this.fileName = fileName
    }

}