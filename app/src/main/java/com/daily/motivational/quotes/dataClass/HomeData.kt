package com.daily.motivational.quotes.dataClass

import android.app.Activity
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.androidnetworking.interfaces.DownloadProgressListener
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.model.ImageClass
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import es.dmoral.toasty.Toasty
import java.io.File
import java.util.*


class HomeData {

    companion object {
        var pageCount = 1
        var mainArrayList = ArrayList<ImageClass>()
        var BASE_PATH =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()

        fun collapse(v: View) {
            val initialHeight = v.measuredHeight
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    if (interpolatedTime == 1f) {
                        v.visibility = View.GONE
                    } else {
                        v.layoutParams.height =
                            initialHeight - (initialHeight * interpolatedTime).toInt()
                        v.requestLayout()
                    }
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

// Collapse speed of 1dp/ms
            a.duration = 500
            v.startAnimation(a)
        }

        fun expand(v: View) {
            val matchParentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec((v.parent as View).width, View.MeasureSpec.EXACTLY)
            val wrapContentMeasureSpec =
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            v.measure(matchParentMeasureSpec, wrapContentMeasureSpec)
            val targetHeight = v.measuredHeight

// Older versions of android (pre API 21) cancel animations for views with a height of 0.
            v.layoutParams.height = 1
            v.visibility = View.VISIBLE
            val a: Animation = object : Animation() {
                override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                    v.layoutParams.height =
                        if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT else (targetHeight * interpolatedTime).toInt()
                    v.requestLayout()
                }

                override fun willChangeBounds(): Boolean {
                    return true
                }
            }

// Expansion speed of 1dp/ms
            a.duration = 500
            v.startAnimation(a)
        }


        fun share(imgClass: ImageClass, activity: Activity) {
            var bmp: Bitmap? = null
            val target: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    bmp = bitmap
                    val path: String = MediaStore.Images.Media.insertImage(
                        activity.contentResolver,
                        bmp,
                        "Quote Of The Day " + System.currentTimeMillis(),
                        null
                    )
                    Log.d("Path", path)
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.putExtra(Intent.EXTRA_TEXT, "Hey download this image")
                    val screenshotUri = Uri.parse(path)
                    intent.putExtra(Intent.EXTRA_STREAM, screenshotUri)
                    intent.type = "image/*"
                    activity.startActivity(Intent.createChooser(intent, "Share image via..."))
                }

                override fun onBitmapFailed(errorDrawable: Drawable) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }
            }
            val url = imgClass.image
            Picasso.with(activity).load(url).into(target)

        }

        fun save(imgClass: ImageClass, activity: Activity) {
            val dir: File
            val imgFile: File
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val cw = ContextWrapper(activity)
                dir = File(
                    cw.getExternalFilesDir(Environment.DIRECTORY_DCIM),
                    activity.resources.getString(R.string.app_name)
                )
            } else {
//                dir = File(BASE_PATH, activity.resources.getString(R.string.app_name))
                val root = Environment.getExternalStorageDirectory().absolutePath.toString()
                val myDir = File(root, "DCIM")
                myDir.mkdirs()

                dir = File(myDir, "Camera")
            }
            if (!dir.exists()) {
                dir.mkdirs()
            }

            AndroidNetworking.download(
                imgClass.image,
                dir.absolutePath,
                "Motivational_quotes" + System.currentTimeMillis() + ".jpg"
            )
                .setTag("downloadTest")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .setDownloadProgressListener(object : DownloadProgressListener {
                    override fun onProgress(bytesDownloaded: Long, totalBytes: Long) {
                    }
                })
                .startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                        Toasty.success(activity, "Image Downloaded Successfully!").show()
                    }

                    override fun onError(error: ANError?) {
                        Toasty.error(activity, "Something went wrong!").show()
                        Log.e("error save", error?.message.toString())
                    }
                })
        }
    }
}