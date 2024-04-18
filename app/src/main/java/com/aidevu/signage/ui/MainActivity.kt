package com.aidevu.signage.ui

import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aidevu.signage.R
import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.adapter.main.MainOrderAdapter
import com.aidevu.signage.databinding.ActivityMainBinding
import com.aidevu.signage.ui.base.BaseActivity
import com.aidevu.signage.ui.settings.SettingsAdAddActivity
import com.aidevu.signage.ui.settings.SettingsAdOrderEditActivity
import com.aidevu.signage.ui.video.LoopingViewActivity
import com.aidevu.signage.utils.Constant
import com.aidevu.signage.utils.Log
import com.aidevu.signage.utils.Utils
import com.aidevu.signage.viewmodel.MainViewModel
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private val imageList by lazy { ArrayList<Ad>() }
    private val videoList by lazy { ArrayList<Ad>() }
    private lateinit var listAll: List<Ad>
    private val mainOrderAdapter by lazy { MainOrderAdapter() }
    var backPressedTime: Long = 0

    override fun createViewModel(): MainViewModel {
        return ViewModelProvider(this)[MainViewModel::class.java]
    }

    override fun createViewBinding(layoutInflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("onCreate")

        observeData()


        binding.addAd.setOnClickListener {
            var intent = Intent(this, SettingsAdAddActivity::class.java)
            startActivity(intent)
        }

        binding.orderAd.setOnClickListener {
            var intent = Intent(this, SettingsAdOrderEditActivity::class.java)
            startActivity(intent)
        }

        binding.addAdRight.setOnClickListener {
            var intent = Intent(this, SettingsAdAddActivity::class.java)
            startActivity(intent)
        }

        binding.orderAdRight.setOnClickListener {
            var intent = Intent(this, SettingsAdOrderEditActivity::class.java)
            startActivity(intent)
        }


        binding.boardStart.setOnClickListener {
            var intent = Intent(this, LoopingViewActivity::class.java)
            startActivity(intent)
        }



        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        val width = size.x
        val height = size.y

        binding.displayInfo.text = "${getString(R.string.str_screen_size)}\nwidth : $width, height : $height"
        Log.d("${getString(R.string.str_screen_size)}\nwidth : $width, height : $height")


        binding.fullModeOnOff.isChecked = prefs.getFullMode()

        //switch 체크 이벤트
        binding.fullModeOnOff.setOnCheckedChangeListener { p0, isChecked ->
            if (isChecked) {
                prefs.setFullMode(true)
            } else {
                prefs.setFullMode(false)
            }
        }


        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    private fun init() {
        scope.launch {
            setItemCount(viewModel.getOrderAscList())


            listAll = viewModel.getOrderDescList().sortedBy {
                it.getOrder()
            }

            for (i in listAll.indices) {
                Log.d("getOrderAscList : ${listAll[i].getOrder()}, ${listAll[i].getFileName()}")
            }

            if (listAll != null && listAll.isNotEmpty()) {
                binding.orderCount.visibility = View.GONE
                binding.ryOrderView.visibility = View.VISIBLE
            } else {
                Log.d("먼저 광고를 추가해 주세요.")
                binding.orderCount.visibility = View.VISIBLE
                binding.ryOrderView.visibility = View.GONE
            }

            initRecyclerView()
        }
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun setListItems(list: List<Ad>) {
        mainOrderAdapter.submitList(list)
    }

    private fun initRecyclerView() {
        binding.ryOrderView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.ryOrderView.adapter = mainOrderAdapter

        setListItems(listAll)
    }

    private fun setItemCount(ad: List<Ad>) {
        imageList.clear()
        videoList.clear()

        for(i in ad.indices) {
            if(ad[i].getFileType() == Constant.image) {
                imageList.add(ad[i])
            }else{
                videoList.add(ad[i])
            }
        }
        binding.imageCount.text = getString(R.string.str_image_count, imageList.size.toString())
        binding.videoCount.text = getString(R.string.str_video_count, videoList.size.toString())
        binding.orderCount.text = getString(R.string.str_image_video_count, (videoList.size + videoList.size).toString())

    }

    override fun observeData() {
        viewModel.getAdList().observe(this) { it ->
            if (it == null || it.size === 0) {
                Log.d("변한 데이터 없음")
            } else {
                Log.d("---------------------------------------------------------------------------------")
                Log.d("getData Size : " + it.size)
                for (i: Int in it.indices) {
                    Log.d("getFilePath2 : " + it[i].getFilePath())
                    Log.d("getFileType2 : " + it[i].getFileType())
                    Log.d("getDuration2 : " + it[i].getDuration())
                    Log.d("getId2 : " + it[i].getId())
                    Log.d("getIndex2 : " + it[i].getIndex())
                    Log.d("getOrder2 : " + it[i].getOrder())
                }
                Log.d("---------------------------------------------------------------------------------")
            }
            hideDialog(binding.loading.progressBar)
        }

        viewModel.getList().observe(this) { usrgAlctDVOS ->
            if (usrgAlctDVOS == null) {
                showErrorDialog(binding.loading.progressBar, getString(R.string.str_app_dialog_info), getString(
                        R.string.str_app_dialog_no_data
                    ))
            } else {
                // 성공
            }
            hideDialog(binding.loading.progressBar)
        }

        viewModel.getErrorState().observe(this) { errorStr ->
            showErrorDialog(
                binding.loading.progressBar,
                getString(R.string.str_app_dialog_error),
                errorStr.errorMessage
            )
        }
    }

    override fun onBackPressed() {
        if(backPressedTime + 2600 > System.currentTimeMillis()){
            super.onBackPressed()
            finish()
        }else{
            Utils.showToastError(this@MainActivity, this@MainActivity.getString(R.string.str_finish))
        }
        backPressedTime = System.currentTimeMillis()
    }
}