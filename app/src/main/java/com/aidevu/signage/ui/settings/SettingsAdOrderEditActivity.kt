package com.aidevu.signage.ui.settings

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.aidevu.signage.R
import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.adapter.settings.ItemTouchCallback
import com.aidevu.signage.adapter.settings.SettingsAdOrderEditAdapter
import com.aidevu.signage.databinding.ActivitySettingsAdOrderEditBinding
import com.aidevu.signage.repository.Repository
import com.aidevu.signage.ui.base.BaseActivity
import com.aidevu.signage.utils.Log
import com.aidevu.signage.utils.Utils
import com.aidevu.signage.viewmodel.settings.SettingsAdOrderEditViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SettingsAdOrderEditActivity :
    BaseActivity<ActivitySettingsAdOrderEditBinding, SettingsAdOrderEditViewModel>() {

    private lateinit var listAll: List<Ad>
    private val settingsAdOrderEditAdapter by lazy { SettingsAdOrderEditAdapter() }
    private val itemTouchHelper by lazy { ItemTouchHelper(ItemTouchCallback(settingsAdOrderEditAdapter)) }
    private val scope = CoroutineScope(Dispatchers.Main)
    private lateinit var activity: Activity
    @Inject
    lateinit var repository: Repository

    override fun createViewModel(): SettingsAdOrderEditViewModel {
        return ViewModelProvider(this)[SettingsAdOrderEditViewModel::class.java]
    }

    override fun createViewBinding(layoutInflater: LayoutInflater): ActivitySettingsAdOrderEditBinding {
        return ActivitySettingsAdOrderEditBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        observeData()

        binding.boardSave.setOnClickListener {
            Log.d("순서 변경 save")

            scope.launch {
                listAll = settingsAdOrderEditAdapter.getList()

                for(i in listAll.indices) {
                    Log.d("순서 변경 save : " + listAll[i].getOrder() + ", " + listAll[i].getFileName()) // 2, b

                    editData(listAll[i].getId()!!, (i + 1)) // 2, 1
                    delay(100)
                }

                Utils.showToast(activity, activity.getString(R.string.str_order_save))
            }
        }
        binding.back.setOnClickListener {
            finish()
        }

        scope.launch {
            listAll = viewModel.getOrderDescList().sortedBy {
                it.getOrder()
            }

            for (i in listAll.indices) {
                Log.d("getOrderAscList : ${listAll[i].getOrder()}, ${listAll[i].getFileName()}")
            }

            if (listAll != null && listAll.isNotEmpty()) {
                binding.rlEmptyTextView.visibility = View.GONE
                binding.rvAdOrder.visibility = View.VISIBLE
            } else {
                Log.d("먼저 광고를 추가해 주세요.")
                binding.rlEmptyTextView.visibility = View.VISIBLE
                binding.rvAdOrder.visibility = View.GONE
            }

            initRecyclerView()
        }
    }

    private fun editData(id: Int, order: Int) {
        scope.launch {
            viewModel.updateData(id, order)
        }
    }

    private fun initRecyclerView() {
        binding.rvAdOrder.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvAdOrder.adapter = settingsAdOrderEditAdapter

        // 리싸이클러뷰에 itemTouchHelper 연결
        itemTouchHelper.attachToRecyclerView(binding.rvAdOrder)

        setListItems(listAll)
    }

    private fun setListItems(list: List<Ad>) {
        settingsAdOrderEditAdapter.submitList(list)
    }

    override fun observeData() {
        viewModel.getAdList().observe(this) { it ->
            if (it == null || it.size === 0) {
                Log.d("변한 데이터 없음")
            } else {
//                Log.d("---------------------------------------------------------------------------------")
//                Log.d("getData Size : " + it.size)
//                for (i: Int in it.indices) {
//                    Log.d("getFilePath : " + it[i].getFilePath())
//                    Log.d("getFileType : " + it[i].getFileType())
//                    Log.d("getDuration : " + it[i].getDuration())
//                    Log.d("getId : " + it[i].getId())
//                    Log.d("getIndex : " + it[i].getIndex())
//                    Log.d("getOrder : " + it[i].getOrder())
//                }
//                Log.d("---------------------------------------------------------------------------------")

//                setListItems(it)
            }
            hideDialog(binding.loading.progressBar)

        }
    }
}