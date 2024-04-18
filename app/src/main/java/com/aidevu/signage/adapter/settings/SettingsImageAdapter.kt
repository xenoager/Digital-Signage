package com.aidevu.signage.adapter.settings

import android.app.Activity
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.databinding.ItemAdEditImageBinding
import com.aidevu.signage.interfaces.MyOnClickListener
import com.aidevu.signage.utils.Constant
import com.aidevu.signage.utils.Utils

class SettingsImageAdapter(private val myOnClickListener: MyOnClickListener, private val activity: Activity) : RecyclerView.Adapter<SettingsImageAdapter.ViewHolder>() {

    private val list = ArrayList<Ad>()
    private lateinit var binding: ItemAdEditImageBinding

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
        binding = ItemAdEditImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setItem(list[position], position)
    }

    inner class ViewHolder(val binding: ItemAdEditImageBinding) : RecyclerView.ViewHolder(binding.root) {

        fun setItem(item: Ad, position: Int) {
            binding.tvOrderEditText1.movementMethod = ScrollingMovementMethod()

            binding.tvOrderEditText.setOnTouchListener(object: OnTouchListener {
                override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                    p0?.parent?.requestDisallowInterceptTouchEvent(true);
                    return false
                }

            })

            binding.tvOrderEditText.text = item.getFileName()
            if(item.getFilePath() != null && item.getFilePath() != "") {
                binding.tvOrderEditText1.text = item.getFilePath()
                binding.imgThumb.setImageBitmap(Utils.pathToBitmap(item.getFilePath()))
            }
            if(item.getDuration() != 0) {
                binding.tvOrderEditText2.text = item.getDuration().toString()
            }else{
                binding.tvOrderEditText2.text = "1"
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