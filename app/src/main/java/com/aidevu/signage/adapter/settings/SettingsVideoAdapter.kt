package com.aidevu.signage.adapter.settings

import android.app.Activity
import android.net.Uri
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.interfaces.MyOnClickListener
import com.aidevu.signage.databinding.ItemAdEditVideoBinding
import com.aidevu.signage.utils.Constant
import com.bumptech.glide.Glide
import java.io.File

class SettingsVideoAdapter(private val myOnClickListener: MyOnClickListener, private val activity: Activity) : RecyclerView.Adapter<SettingsVideoAdapter.ViewHolder>() {

    private val list = ArrayList<Ad>()
    private lateinit var binding: ItemAdEditVideoBinding

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemAdEditVideoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(list[position], position)
    }

    inner class ViewHolder(val binding: ItemAdEditVideoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setItem(item: Ad, position: Int) {
            binding.tvOrderEditText1.movementMethod = ScrollingMovementMethod()
            binding.tvOrderEditText.text = item.getFileName()
            if(item.getFilePath() != null && item.getFilePath() != "") {
                binding.tvOrderEditText1.text = item.getFilePath()
                Glide
                    .with(activity)
                    .asBitmap()
                    .load(Uri.fromFile(File(item.getFilePath())))
                    .into(binding.imgThumb);
            }

            binding.deleteItem.setOnClickListener {
                myOnClickListener.onItemItemDelete(position, Constant.image, item)
            }

            binding.btnEdit.setOnClickListener {
                myOnClickListener.onItemSearch(position, Constant.image, item)
            }
        }
    }
}