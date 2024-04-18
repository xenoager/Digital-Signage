package com.aidevu.signage.adapter.settings

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.databinding.ItemAdOrderEditBinding

class SettingsAdOrderEditAdapter : RecyclerView.Adapter<SettingsAdOrderEditAdapter.ViewHolder>(),
    ItemTouchHelperListener {

    private val list = ArrayList<Ad>()

    override fun onItemMove(from: Int, to: Int) {
        val item: Ad = list[from]
        list.removeAt(from)
        list.add(to, item)
        notifyItemMoved(from, to)
        getList()
    }

    fun submitList(list: List<Ad>) {
        this.list.clear()
        this.list.addAll(list)
        //notifyItemRangeChanged(0, list.size)
        notifyDataSetChanged()
    }

    fun getList(): List<Ad> {
        return this.list
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemAdOrderEditBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.setItem(list[position], position)

    inner class ViewHolder(private val binding: ItemAdOrderEditBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setItem(item: Ad, position: Int) {
            binding.tvOrderEditText.text = item.getFileName()
        }

    }

}