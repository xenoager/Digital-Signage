package com.aidevu.signage.interfaces

import com.aidevu.signage.adapter.db.ad.Ad

interface MyOnClickListener {
    fun onItemItemDelete(position: Int, type: String, ad: Ad)
    fun onItemPathDelete(position: Int, type: String, ad: Ad)
    fun onItemDurationDelete(position: Int, type: String, ad: Ad)
    fun onItemSearch(position: Int, type: String, ad: Ad)
}