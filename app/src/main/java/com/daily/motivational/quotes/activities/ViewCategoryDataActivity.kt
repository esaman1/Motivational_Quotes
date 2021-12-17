package com.daily.motivational.quotes.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.activities.InnerCategoryActivity.Companion.pageCount
import com.daily.motivational.quotes.adapters.InnerCategoryDataAdapter
import com.daily.motivational.quotes.adapters.InnerCategoryDataAdapter.Companion.mArrayList
import com.daily.motivational.quotes.dataClass.CustomSnapHelper
import com.daily.motivational.quotes.dataClass.HomeData
import com.daily.motivational.quotes.databinding.ActivityViewCategoryDataBinding
import com.daily.motivational.quotes.model.ImageClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException
import java.util.*


class ViewCategoryDataActivity : AppCompatActivity(), View.OnClickListener {

    private var previousTotal = 0
    private var loading = true
    private val visibleThreshold = 5
    var firstVisibleItem = 0
    var visibleItemCount: kotlin.Int = 0
    var totalItemCount: kotlin.Int = 0
    var arrayList = ArrayList<ImageClass>()
    var json1: String? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var mFavouriteImageList = ArrayList<ImageClass>()
    var gson: Gson? = null
    var exist = false
    var pos = 0
    var position = 0

    companion object {
        var currentPosition = 1
        var from: String? = null
        var cid: String? = null
        var category: String? = null
        var mActivity: Activity? = null
        var adapter: InnerCategoryDataAdapter? = null
        var binding: ActivityViewCategoryDataBinding? = null

        fun getInnerData(cid: String) {
            AndroidNetworking.post("https://songskalyrics.com/app/quotesinhindi/admin/fetch_cat_quotes_new.php")
                .addQueryParameter("page", pageCount.toString())
                .addBodyParameter("cid", cid.toString())
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({ hideProgressView() }, 2000)
                        if (response.toString().trim { it <= ' ' } == "[]") {
                            pageCount = -1
//                            Log.e("cat response :- ", response.toString())
                            Toast.makeText(mActivity, "No More Data Here!!!", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            pageCount++
//                            Log.e("cat response :- ", response.toString())
//                            Log.e("cat length :- ", response.length().toString())
                            for (i in 0 until response.length()) {
                                try {
                                    val `object` = response.getJSONObject(i)
                                    val popularModel =
                                        Gson().fromJson(`object`.toString(), ImageClass::class.java)
                                    adapter?.add(popularModel)
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }

                    override fun onError(error: ANError) {
                        hideProgressView()

//                        Log.e("cat error:- ", anError.message.toString())
                        if (error.errorDetail.equals("connectionError")) {
                            mActivity?.let {
                                Toasty.info(
                                    it,
                                    "No Internet Found!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else if (error.errorDetail.equals("parseError")) {
                            mActivity?.let {
                                Toasty.info(
                                    it,
                                    "No More Data Here!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            mActivity?.let {
                                Toasty.info(
                                    it,
                                    "Something went wrong!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                })
        }

        fun hideProgressView() {
            binding?.progressBar?.visibility = View.INVISIBLE
        }

        fun showProgressView() {
            binding?.progressBar?.visibility = View.VISIBLE
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(
                this@ViewCategoryDataActivity,
                R.layout.activity_view_category_data
            )

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        mActivity = this@ViewCategoryDataActivity
        adapter = InnerCategoryDataAdapter(this@ViewCategoryDataActivity)
        var mLayoutManager =
            LinearLayoutManager(
                this@ViewCategoryDataActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )


//        val snapHelper: SnapHelper = PagerSnapHelper()
//        snapHelper.attachToRecyclerView(binding?.picker)
        val mSnapHelper = CustomSnapHelper()
        mSnapHelper.attachToRecyclerView(binding?.picker)

        val args = intent.getBundleExtra("BUNDLE")
        if (args != null) {
            arrayList = (args.getSerializable("modelArray") as ArrayList<ImageClass>?)!!
            adapter?.newArray()
            cid = args.getString("cid")
            position = args.getInt("position")
            category = args.getString("cname")

//            cid?.let { Log.e(" cat cid view: ", it) }
//            Log.e("cname " + category, "page " + pageCount)

            binding?.title?.text = category
            for (i in arrayList.indices) {
                adapter?.add(arrayList[i])
            }
            binding?.picker?.layoutManager = mLayoutManager
            binding?.picker?.adapter = adapter
            binding?.picker?.scrollToPosition(position)
            binding?.picker?.smoothScrollBy(1, 0)

        }

        binding?.picker?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (binding?.mBottomLL?.isVisible == true) {
                    HomeData.collapse(binding?.mBottomLL!!)
                }

                visibleItemCount = binding?.picker?.childCount!!
                totalItemCount = mLayoutManager.itemCount
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition()

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false
                        previousTotal = totalItemCount
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)
                ) {
                    showProgressView()
                    cid?.let { getInnerData(it) }
                    loading = true
                }
            }
        })

        binding?.mFavourite?.setOnClickListener(this)
        binding?.mSave?.setOnClickListener(this)
        binding?.mShare?.setOnClickListener(this)
        binding?.mCardView?.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        val imageClass = mArrayList.get(currentPosition)
        when (v.id) {
            R.id.mFavourite -> {

                favouriteOperation(imageClass)
            }
            R.id.mShare -> {
                HomeData.share(imageClass, this@ViewCategoryDataActivity)
            }
            R.id.mSave -> {
                HomeData.save(imageClass, this@ViewCategoryDataActivity)
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
            Toasty.success(this@ViewCategoryDataActivity, "Add to favourites!").show()
        } else {
            mFavouriteImageList.removeAt(pos)
            exist = false
            binding?.mFavourite?.setImageDrawable(
                mActivity?.resources?.getDrawable(
                    R.drawable.ic_fav
                )
            )
            Toasty.success(this@ViewCategoryDataActivity, "Remove from favourites!").show()
        }

//        Log.e("fav_size", mFavouriteImageList.size.toString())
        json1 = gson?.toJson(mFavouriteImageList)
        editor?.putString("Fav_Image", json1)
        editor?.apply()

        val lbm = LocalBroadcastManager.getInstance(this@ViewCategoryDataActivity)
        val localIn = Intent("REFRESH")
        lbm.sendBroadcast(localIn)

    }

    override fun onBackPressed() {
        pageCount = 1
        super.onBackPressed()
    }


}