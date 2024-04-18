package com.aidevu.signage.interfaces

interface DialogValueOnClickListener {
    fun onClick(fileName: String, filePath: String, duration: String)
    fun onCancel()
    fun onSearch(type: String)
}