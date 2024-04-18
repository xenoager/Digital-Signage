package com.aidevu.signage.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.aidevu.signage.R
import com.aidevu.signage.adapter.db.ad.Ad
import com.aidevu.signage.interfaces.MyOnClickListener
import com.aidevu.signage.adapter.settings.SettingsImageAdapter
import com.aidevu.signage.adapter.settings.SettingsVideoAdapter
import com.aidevu.signage.databinding.ActivitySettingsAdAddBinding
import com.aidevu.signage.interfaces.DialogOnClickListener
import com.aidevu.signage.interfaces.DialogValueOnClickListener
import com.aidevu.signage.repository.Repository
import com.aidevu.signage.ui.base.BaseActivity
import com.aidevu.signage.ui.dialog.AddImageDialogView
import com.aidevu.signage.ui.dialog.AddVideoDialogView
import com.aidevu.signage.ui.dialog.AlertDialogView
import com.aidevu.signage.utils.Constant
import com.aidevu.signage.utils.Log
import com.aidevu.signage.utils.Utils
import com.aidevu.signage.viewmodel.settings.SettingsAdAddViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import javax.inject.Inject

@AndroidEntryPoint
class SettingsAdAddActivity : BaseActivity<ActivitySettingsAdAddBinding, SettingsAdAddViewModel>() {

    @Inject
    lateinit var repository: Repository

    private val imageAdapter by lazy { SettingsImageAdapter(imageOnClickListener, this) }

    private val videoAdapter by lazy { SettingsVideoAdapter(videoOnClickListener, this) }

    private val imageList by lazy { ArrayList<Ad>() }
    private val videoList by lazy { ArrayList<Ad>() }

    private lateinit var showAddImageDialog: AddImageDialogView
    private lateinit var showAddVideoDialog: AddVideoDialogView
    var type: String? = null
    var imageUri: Uri? = null
    private val scope = CoroutineScope(Dispatchers.Main)
    private var pfd: ParcelFileDescriptor? = null
    private var fileInputStream: FileInputStream? = null
    private lateinit var context: Context

    private var imageOnClickListener = object: MyOnClickListener {
        override fun onItemItemDelete(position: Int, type: String, ad: Ad) {
            Log.d("image onItemItemDelete : $position")
            val dialogView = AlertDialogView(context, object : DialogOnClickListener {
                override fun onClick(str: String?) {
                    scope.launch {
                        viewModel.delete(ad.getFilePath(), ad.getId()!!)
                        deleteImage(position, type)
                        isEmptyListCheck(Constant.image)
                    }
                }

                override fun onCancel() {

                }
            }, AlertDialogView.TYPE_TWO_BUTTON)
            dialogView.setContent(getString(R.string.str_remove))
            dialogView.show()
        }

        override fun onItemPathDelete(position: Int, type: String, ad: Ad) {
            Log.d("image onItemPathDelete : $position")
        }

        override fun onItemDurationDelete(position: Int, type: String, ad: Ad) {
            Log.d("image onItemDurationDelete : $position")
        }

        override fun onItemSearch(position: Int, type: String, ad: Ad) {
            Log.d("image onItemSearch : $position")
            showEditImageDialog(binding.loading.progressBar, position, type, ad)
        }
    }

    private var videoOnClickListener = object: MyOnClickListener {
        override fun onItemItemDelete(position: Int, type: String, ad: Ad) {
            Log.d("video onItemItemDelete : $position")
            val dialogView = AlertDialogView(context, object : DialogOnClickListener {
                override fun onClick(str: String?) {
                    scope.launch {
                        viewModel.delete(ad.getFilePath(), ad.getId()!!)
                        deleteVideo(position, type)
                        isEmptyListCheck(Constant.video)
                    }
                }

                override fun onCancel() {

                }
            }, AlertDialogView.TYPE_TWO_BUTTON)
            dialogView.setContent(getString(R.string.str_remove))
            dialogView.show()
        }

        override fun onItemPathDelete(position: Int, type: String, ad: Ad) {
            Log.d("video onItemPathDelete : $position")
        }

        override fun onItemDurationDelete(position: Int, type: String, ad: Ad) {
            Log.d("video onItemDurationDelete : $position")
        }

        override fun onItemSearch(position: Int, type: String, ad: Ad) {
            Log.d("video onItemSearch : $position")
            showEditVideoDialog(binding.loading.progressBar, position, type, ad)
        }
    }

    override fun createViewModel(): SettingsAdAddViewModel {
        return ViewModelProvider(this)[SettingsAdAddViewModel::class.java]
    }

    override fun createViewBinding(layoutInflater: LayoutInflater): ActivitySettingsAdAddBinding {
        return ActivitySettingsAdAddBinding.inflate(layoutInflater)
    }

    private fun deleteImage(position: Int, type: String){
        imageList.removeAt(position);
        imageAdapter.submitList(imageList)
    }

    private fun deleteVideo(position: Int, type: String){
        videoList.removeAt(position);
        videoAdapter.submitList(videoList)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        observeData()


        initView()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.rlImage.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rlImage.adapter = imageAdapter

        binding.rlVideo.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rlVideo.adapter = videoAdapter


        scope.launch {
            setListItems(viewModel.getOrderAscList())

            isEmptyListCheck(Constant.image)
            isEmptyListCheck(Constant.video)
        }
    }

    private fun initView() {
        binding.back.setOnClickListener {
            finish()
        }

        binding.addImage.setOnClickListener {
            Log.d("이미지 추가 팝업 띄운다.")
            showAddImageDialog(binding.loading.progressBar, 1)
        }

        binding.addVideo.setOnClickListener {
            Log.d("비디오 추가 팝업 띄운다.")
            showAddVideoDialog(binding.loading.progressBar, 1)
        }

        binding.boardSave.setOnClickListener {
            imageAdapter.notifyDataSetChanged()
            for (i in imageList.indices) {
                Log.e("$i, imageList path : " + imageList[i].getFilePath())
                Log.e("$i, imageList duration : " + imageList[i].getDuration())
            }

            for (i in videoList.indices) {
                Log.e("$i, videoList path : " + videoList[i].getFilePath())
                var retriever = MediaMetadataRetriever();
                retriever.setDataSource(videoList[i].getFilePath());
                var duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toDouble()?.let {
                    it / 1000
                }
                retriever.release();
                Log.e("$i, videoList duration : ${Utils.roundUpDigit(duration!!, 0).toInt()}")
            }
        }
    }



    private fun editData(type: String, id: Int, fileName: String, path: String, duration: Int) {
        scope.launch {
            var result = viewModel.updateData(type, id, fileName, path, duration)
            Log.d("updateData result : $result")
        }
    }

    private fun addData(type: String, fileName: String, path: String, duration: Int) {
        scope.launch {
            var it = viewModel.getOrderAscList()
            var lastIndex = 0
            try {
                lastIndex = it.maxOfOrNull { it.getIndex()}!!
                Log.d("lastIndex : $lastIndex")
            }catch (e: Exception){
                e.printStackTrace()
            }

            var lastOrder = 0
            try {
                lastOrder = it.maxOfOrNull { it.getOrder()}!!
                Log.d("lastOrder : $lastOrder")
            }catch (e: Exception){
                e.printStackTrace()
            }
            var ad = Ad(lastIndex + 1, type, fileName, path, duration, lastOrder + 1)
            viewModel.getAdList(ad)
        }
    }

    private fun showAddVideoDialog(view: View, position: Int) {
        showAddVideoDialog = AddVideoDialogView(this, object : DialogValueOnClickListener {
            override fun onClick(fileName: String, filePath: String, duration: String) {
                //추가하고

                var retriever = MediaMetadataRetriever();
                retriever.setDataSource(filePath);
                var duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toDouble()?.let {
                    it / 1000
                }
                retriever.release();

                var durationTemp = Utils.roundUpDigit(duration!!, 0).toInt()
                Log.e("videoList path : $filePath")
                Log.e("videoList durationTemp : $durationTemp")

                addData(Constant.video, fileName, filePath, durationTemp)
                refreshVideo()
                hideDialog(view)
            }

            override fun onCancel() {
                hideDialog(view)
            }

            override fun onSearch(type: String) {
                openGallery(Constant.video)
            }
        }, AlertDialogView.TYPE_TWO_BUTTON, position, Constant.add)
        showAddVideoDialog.show()
    }


    private fun showAddImageDialog(view: View, position: Int) {
        showAddImageDialog = AddImageDialogView(this, object : DialogValueOnClickListener {
            override fun onClick(fileName: String, filePath: String, duration: String) {
                //추가하고
                addData(Constant.image, fileName, filePath, duration.toInt())
                refreshImage()
                hideDialog(view)
            }

            override fun onCancel() {
                hideDialog(view)
            }

            override fun onSearch(type: String) {
                openGallery(Constant.image)
            }

        }, AlertDialogView.TYPE_TWO_BUTTON, position, Constant.add)
        showAddImageDialog.show()
    }

    private fun showEditVideoDialog(view: View, position: Int, type: String, ad: Ad) {
        showAddVideoDialog = AddVideoDialogView(this, object : DialogValueOnClickListener {
            override fun onClick(fileName: String, filePath: String, duration: String) {
                //수정하고
                Log.d("showEditVideoDialog")

                var retriever = MediaMetadataRetriever();
                retriever.setDataSource(filePath);
                var duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toDouble()?.let {
                    it / 1000
                }
                retriever.release();

                var durationTemp = Utils.roundUpDigit(duration!!, 0).toInt()
                Log.e("videoList path1 : $filePath")
                Log.e("videoList durationTemp1 : $durationTemp")


                editData(Constant.video, ad.getId()!!, fileName, filePath, durationTemp)
                refreshVideo()
                hideDialog(view)
            }

            override fun onCancel() {
                hideDialog(view)
            }

            override fun onSearch(type: String) {
                openGallery(Constant.video)
            }
        }, AlertDialogView.TYPE_TWO_BUTTON, position, Constant.edit)
        showAddVideoDialog.setTitle(getString(R.string.str_video_add_edit))
        showAddVideoDialog.setFileName(ad.getFileName())
        showAddVideoDialog.setFilePath(ad.getFilePath())
        showAddVideoDialog.show()
    }


    private fun showEditImageDialog(view: View, position: Int, type: String, ad: Ad) {
        showAddImageDialog = AddImageDialogView(this, object : DialogValueOnClickListener {
            override fun onClick(fileName: String, filePath: String, duration: String) {
                //수정하고
                Log.d("showEditImageDialog")
                editData(Constant.image, ad.getId()!!, fileName, filePath, duration.toInt())
                refreshImage()
                hideDialog(view)
            }

            override fun onCancel() {
                hideDialog(view)
            }

            override fun onSearch(type: String) {
                openGallery(Constant.image)
            }

        }, AlertDialogView.TYPE_TWO_BUTTON, position, Constant.edit)
        showAddImageDialog.setTitle(getString(R.string.str_image_add_edit))
        showAddImageDialog.setFileName(ad.getFileName())
        showAddImageDialog.setFilePath(ad.getFilePath())
        showAddImageDialog.setFileDuration(ad.getDuration().toString())
        showAddImageDialog.show()
    }

    private fun openGallery(type: String) {
        this.type = type
        var temp: String? = null
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if(Constant.image == type) {
            gallery.type = "image/*";
//            intent.type = "image/*";
            temp = "image/*";
        }else if(Constant.video == type) {
            gallery.type = "video/*";
            temp = "video/*";
        }

        Utils.scanFile(context, Environment.getExternalStorageDirectory(), temp!!)
        pickImageLauncher.launch(gallery)
    }

    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let {
                    imageUri = it
                    if(this.type.equals(Constant.image)){
                        var fileName= saveFile(it, Constant.image + "_" + System.currentTimeMillis())
                        var mOutputDir = getExternalFilesDir(null)
                        val now = System.currentTimeMillis()
                        var filePath = mOutputDir?.path + "/" + fileName
                        Log.d("fileName image : $fileName")
                        Log.d("filePath image : $filePath")
                        showAddImageDialog.setPath(filePath, it)
                    }else{
                        var fileName= saveFile(it, Constant.video + "_" + System.currentTimeMillis())
                        var mOutputDir = getExternalFilesDir(null)
                        val now = System.currentTimeMillis()
                        var filePath = mOutputDir?.path + "/" + fileName
                        Log.d("fileName video : $fileName")
                        Log.d("filePath video : $filePath")
                        showAddVideoDialog.setPath(filePath, it)
                    }

                }
            }
        }

    private fun refreshImage() {
        imageAdapter.submitList(imageList)
        isEmptyListCheck(Constant.image)
    }

    private fun refreshVideo() {
        videoAdapter.submitList(videoList)
        isEmptyListCheck(Constant.video)
    }

    private fun setListItems(ad: List<Ad>) {
//        imageList.add(ImageItem("/path/imageimageimageimageimageimageimageimage/1.png", 0))
//        imageList.add(ImageItem("/path/imageimageimageimageimageimageimageimage/2.png", 0))
        imageList.clear()
        videoList.clear()


        for(i in ad.indices) {
            if(ad[i].getFileType() == Constant.image) {
                imageList.add(ad[i])
            }else{
                videoList.add(ad[i])
            }
        }

        refreshImage()
        refreshVideo()

//        videoList.add(VideoItem("/path/videovideovideovideovideovideovideovideovideo/1.avi"))
//        videoList.add(VideoItem("/path/videovideovideovideovideovideovideovideovideo/2.avi"))
    }



    private fun isEmptyListCheck(type: String) {
        if(type == Constant.image) {
            if (imageList.size != 0) {
                binding.rlImage.visibility = View.VISIBLE
                binding.rlEmptyImage.visibility = View.GONE
            } else {
                binding.rlImage.visibility = View.GONE
                binding.rlEmptyImage.visibility = View.VISIBLE
            }
        }
        if(type == Constant.video) {
            if (videoList.size != 0) {
                binding.rlVideo.visibility = View.VISIBLE
                binding.rlEmptyVideo.visibility = View.GONE
            } else {
                binding.rlVideo.visibility = View.GONE
                binding.rlEmptyVideo.visibility = View.VISIBLE
            }
        }
    }

    override fun observeData() {
        viewModel.getAdList().observe(this) { it ->
            if (it == null || it.size === 0) {
                Log.d("변한 데이터 없음")
            } else {
                Log.d("---------------------------------------------------------------------------------")
                Log.d("getData Size : " + it.size)
                for (i: Int in it.indices) {
                    Log.d("getFilePath1 : " + it[i].getFilePath())
                    Log.d("getFileType1 : " + it[i].getFileType())
                    Log.d("getDuration1 : " + it[i].getDuration())
                    Log.d("getId1 : " + it[i].getId())
                    Log.d("getIndex1 : " + it[i].getIndex())
                    Log.d("getOrder1 : " + it[i].getOrder())
                }
                Log.d("---------------------------------------------------------------------------------")

                setListItems(it)
            }
            hideDialog(binding.loading.progressBar)

        }

    }

    private fun saveFile(uri: Uri, fileName: String): String{
        var mOutputDir = getExternalFilesDir(null)
        val a = getFileName(uri)
        var b = a?.substring(a.lastIndexOf("."), a.length)
        var name = fileName + b
        try {
            pfd = uri.let { contentResolver?.openFileDescriptor(it, "r") }
            fileInputStream = FileInputStream(pfd?.fileDescriptor)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        var newFile: File? = null
        if(fileName!=null) {
            newFile = File(mOutputDir, fileName + b)
        }

        var inChannel: FileChannel? = null
        var outChannel: FileChannel? = null

        try {
            inChannel = fileInputStream?.channel
            outChannel = FileOutputStream(newFile).channel
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        try {
            inChannel?.transferTo(0, inChannel.size(), outChannel)
        } finally {
            inChannel?.close()
            outChannel?.close()
            fileInputStream?.close()
            pfd?.close()
        }

        return name
    }

    @SuppressLint("Range")
    fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver?.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }
}