package com.daily.motivational.quotes.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.activities.ViewActivity.Companion.currentPosition
import com.daily.motivational.quotes.adapters.ShowImageAdapter
import com.daily.motivational.quotes.adapters.ShowImageAdapter.Companion.mArrayList
import com.daily.motivational.quotes.dataClass.CustomSnapHelper
import com.daily.motivational.quotes.dataClass.HomeData
import com.daily.motivational.quotes.databinding.ActivityViewFavouritesBinding
import com.daily.motivational.quotes.model.ImageClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import java.util.*


class ViewFavouritesActivity : AppCompatActivity(), View.OnClickListener {

    var arrayList = ArrayList<ImageClass>()
    var json1: String? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var mFavouriteImageList = ArrayList<ImageClass>()
    var gson: Gson? = null
    var exist = false
    var pos = 0

    companion object {
        var position: Int? = null
        var mActivity: Activity? = null
        var adapter: ShowImageAdapter? = null
        var binding: ActivityViewFavouritesBinding? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this@ViewFavouritesActivity,
                R.layout.activity_view_favourites
            )

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        mActivity = this@ViewFavouritesActivity
        adapter = ShowImageAdapter(this@ViewFavouritesActivity)
        var mLayoutManager =
            LinearLayoutManager(
                this@ViewFavouritesActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )


        val snapHelper: SnapHelper = CustomSnapHelper()
        snapHelper.attachToRecyclerView(binding?.picker)

        val args = intent.getBundleExtra("BUNDLE")
        if (args != null) {
            arrayList.clear()
            arrayList = (args.getSerializable("modelArray") as ArrayList<ImageClass>?)!!

            position = args.getInt("position", 0)
            adapter?.newArray("FAV")
            for (i in arrayList.indices) {
                adapter?.add(arrayList[i])
            }
            binding?.picker?.layoutManager = mLayoutManager
            binding?.picker?.adapter = adapter
            binding?.picker?.scrollToPosition(position!!)
            binding?.picker?.smoothScrollBy(1, 0)
        }

        binding?.picker?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (binding?.mBottomLL?.isVisible == true) {
                    binding?.mBottomLL?.isVisible = false
                }
            }
        })

        binding?.mFavourite?.setOnClickListener(this)
        binding?.mSave?.setOnClickListener(this)
        binding?.mShare?.setOnClickListener(this)
        binding?.mCardView?.setOnClickListener(this)
        binding?.mBack?.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (arrayList.size > 0) {
            Log.e("current pos:", currentPosition.toString())
            Log.e("arraylist size:", arrayList.size.toString())
            val imageClass = arrayList.get(currentPosition)

            when (v.id) {
                R.id.mFavourite -> {
                    favouriteOperation(imageClass)
                }
                R.id.mShare -> {
                    HomeData.share(imageClass, this@ViewFavouritesActivity)
                }
                R.id.mSave -> {
                    HomeData.save(imageClass, this@ViewFavouritesActivity)
                }
                R.id.mBack -> {
                    onBackPressed()
                }
                R.id.mCardView -> {
//                if (binding?.mBottomLL?.isVisible == true) {
//                    collapse(binding?.mBottomLL!!)
//                } else {
//                    expand(binding?.mBottomLL!!)
//                }
                }
            }
        }
    }

    fun favouriteOperation(imgClass: ImageClass) {
        sharedPreferences = getSharedPreferences(
            "Favourites_pref",
            MODE_PRIVATE
        )
        editor = sharedPreferences?.edit()
        try {
            gson = Gson()
            val json = sharedPreferences?.getString("Fav_Image", "")
            val type = object : TypeToken<ArrayList<ImageClass?>?>() {}.type
            mFavouriteImageList = gson?.fromJson(json, type)!!
        } catch (e: Exception) {
            e.message?.let { Log.e("error", it) }
        }
        if (mFavouriteImageList.size > 0) {
            for (i in mFavouriteImageList.indices) {
                if (mFavouriteImageList.get(i).id == imgClass.id) {
                    exist = true
                    pos = i
                    break
                } else {
                    exist = false
                }
            }
        }
        if (!exist) {
            mFavouriteImageList.add(imgClass)
            binding?.mFavourite?.setImageDrawable(
                mActivity?.resources?.getDrawable(
                    R.drawable.ic_liked
                )
            )
            Toasty.success(this@ViewFavouritesActivity, "Add to favourites!").show()
        } else {
            mFavouriteImageList.removeAt(pos)
            arrayList.removeAt(pos)
            mArrayList.removeAt(pos)
            adapter?.notifyDataSetChanged()
            exist = false
            binding?.mFavourite?.setImageDrawable(
                mActivity?.resources?.getDrawable(
                    R.drawable.ic_fav
                )
            )
            Toasty.success(this@ViewFavouritesActivity, "Remove from favourites!").show()
        }

//        Log.e("fav_size", mFavouriteImageList.size.toString())
        json1 = gson?.toJson(mFavouriteImageList)
        editor?.putString("Fav_Image", json1)
        editor?.apply()

        if (mFavouriteImageList.size == 0) {
            onBackPressed()
        }

        val lbm = LocalBroadcastManager.getInstance(this@ViewFavouritesActivity)
        val localIn = Intent("REFRESH")
        lbm.sendBroadcast(localIn)

    }


}