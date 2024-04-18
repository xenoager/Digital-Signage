package com.aidevu.signage.adapter.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.adapter.settings.ItemTouchHelperListener
import com.aidevu.signage.databinding.ItemAdOrderBinding
import java.util.ArrayList

class MainOrderAdapter : RecyclerView.Adapter<MainOrderAdapter.ViewHolder>(),
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
        notifyDataSetChanged()
    }

    fun getList(): List<Ad> {
        return this.list
    }

    override fun getItemCount(): Int = list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(ItemAdOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.setItem(list[position], position)

    inner class ViewHolder(private val binding: ItemAdOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun setItem(item: Ad, position: Int) {
            binding.tvOrder.text = "${(position + 1)}."
            binding.tvOrderEditText.text = item.getFileName()
        }
    }
}