package com.aidevu.signage.utils

import android.app.Activity
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.provider.MediaStore
import android.widget.Toast
import es.dmoral.toasty.Toasty
import java.io.File
import java.io.FileInputStream
import kotlin.math.floor
import kotlin.math.pow

object Utils {

    fun isEmptyTrimmed(source: String?): Boolean {
        return source == null || source.trim().isEmpty()
    }

    fun showToast(activity: Activity, str: String) {
        Toasty.success(activity, str, Toast.LENGTH_SHORT, true).show();
    }

    fun showToastError(activity: Activity, str: String) {
        Toasty.error(activity, str, Toast.LENGTH_SHORT, true).show();
    }

    fun pathToBitmap(path: String?): Bitmap? {
        return try {
            val f = File(path)
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            BitmapFactory.decodeStream(FileInputStream(f), null, options)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getVideoThumbnail(activity: Activity, filePath: String): String? {
        var cur: Cursor? = activity.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, arrayOf(MediaStore.Video.Media._ID),
            MediaStore.Video.Media.DATA + "=?", arrayOf<String>(filePath), null
        )

        cur?.moveToFirst()
        val videoID: String? = cur?.getString(0)
        cur = activity.getContentResolver().query(
            MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Video.Thumbnails.DATA),
            MediaStore.Video.Thumbnails.VIDEO_ID + "=?",
            arrayOf(videoID),
            null
        )

        cur?.moveToFirst()

        return cur?.getString(0)
    }

    fun roundUpDigit(number : Double, digits : Int): Double {
        return floor(number * 10.0.pow(digits.toDouble())) / 10.0.pow(digits.toDouble())
    }

    fun scanFile(context: Context?, f: File, mimeType: String) {
        MediaScannerConnection.scanFile(context, arrayOf(f.absolutePath), arrayOf(mimeType), null)
    }
}