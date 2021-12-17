package com.daily.motivational.quotes.activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
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
import androidx.recyclerview.widget.SnapHelper
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.adapters.ShowImageAdapter
import com.daily.motivational.quotes.adapters.ShowImageAdapter.Companion.mArrayList
import com.daily.motivational.quotes.dataClass.CustomSnapHelper
import com.daily.motivational.quotes.dataClass.HomeData.Companion.collapse
import com.daily.motivational.quotes.dataClass.HomeData.Companion.pageCount
import com.daily.motivational.quotes.dataClass.HomeData.Companion.save
import com.daily.motivational.quotes.dataClass.HomeData.Companion.share
import com.daily.motivational.quotes.databinding.ActivityViewBinding
import com.daily.motivational.quotes.model.ImageClass
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException
import java.util.*


class ViewActivity : AppCompatActivity(), View.OnClickListener {

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

        var mActivity: Activity? = null
        var adapter: ShowImageAdapter? = null
        var binding: ActivityViewBinding? = null
        var dataLength = 0
        fun getData() {
            AndroidNetworking.get("https://songskalyrics.com/app/quotesinhindi/admin/fetch_quotes_new.php")
                .addQueryParameter("page", pageCount.toString())
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({ hideProgressView() }, 2000)
                        if (response.toString().trim { it <= ' ' } == "[]") {
                            pageCount = -1
//                            Log.e("Home response :- ", response.toString())
                        } else {
                            pageCount++
//                            Log.e("Home response :- ", response.toString())
//                        Log.e("Home length :- ", response.length().toString())
                            dataLength = response.length()
                            for (i in 0 until response.length()) {
                                try {
                                    val `object` = response.getJSONObject(i)
                                    var popularModel =
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

//                        Log.e("Home error:- ", anError.message.toString())
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
        binding = DataBindingUtil.setContentView(this@ViewActivity, R.layout.activity_view)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        mActivity = this@ViewActivity
        adapter = ShowImageAdapter(this@ViewActivity)
        var mLayoutManager =
            LinearLayoutManager(this@ViewActivity, LinearLayoutManager.HORIZONTAL, false)

        val args = intent.getBundleExtra("BUNDLE")
        if (args != null) {
            arrayList = (args.getSerializable("modelArray") as ArrayList<ImageClass>?)!!
            position = args.getInt("position")
//
//            Log.e("mArraylist in view", arrayList.size.toString())
//            Log.e("position in view", position.toString())
//            Log.e("id in view", arrayList[position].id.toString())

            binding?.picker?.layoutManager = mLayoutManager
            adapter?.newArray("HOME")
            for (i in arrayList.indices) {
                adapter?.add(arrayList[i])
            }
            binding?.picker?.adapter = adapter
            val snapHelper: SnapHelper = CustomSnapHelper()
            snapHelper.attachToRecyclerView(binding?.picker)

            runOnUiThread(Runnable {
                binding?.picker?.scrollToPosition(position)
                binding?.picker?.smoothScrollBy(1, 0)
            })
        }

        binding?.mBack?.setOnClickListener {
            onBackPressed()
        }

        binding?.picker?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

                if (binding?.mBottomLL?.isVisible == true) {
                    collapse(binding?.mBottomLL!!)
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
                    getData()
                    loading = true
                }
            }
        })

        binding?.mFavourite?.setOnClickListener(this)
        binding?.mSave?.setOnClickListener(this)
        binding?.mShare?.setOnClickListener(this)
        binding?.mCardView?.setOnClickListener(this)



        loadAd()
    }

    var imageClass: ImageClass? = null
    override fun onClick(v: View) {
        imageClass = mArrayList.get(currentPosition)

        when (v.id) {
            R.id.mFavourite -> {
                favouriteOperation(imageClass!!)
            }
            R.id.mShare -> {
                share(imageClass!!, this@ViewActivity)
            }
            R.id.mSave -> {
                showInterstitial()
//                save(imageClass, this@ViewActivity)
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
            Toasty.success(this@ViewActivity, "Add to favourites!").show()
        } else {
            mFavouriteImageList.removeAt(pos)
            exist = false

            binding?.mFavourite?.setImageDrawable(
                mActivity?.resources?.getDrawable(
                    R.drawable.ic_fav
                )
            )
            Toasty.success(this@ViewActivity, "Remove from favourites!").show()
        }

//        Log.e("fav_size", mFavouriteImageList.size.toString())
        json1 = gson?.toJson(mFavouriteImageList)
        editor?.putString("Fav_Image", json1)
        editor?.apply()

        val lbm = LocalBroadcastManager.getInstance(this@ViewActivity)
        val localIn = Intent("REFRESH")
        lbm.sendBroadcast(localIn)
    }

    private var interstitialAd: InterstitialAd? = null
    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    // The mInterstitialAd reference will be null until
                    // an ad is loaded.
                    this@ViewActivity.interstitialAd = interstitialAd

//                    Toast.makeText(this@ViewActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
                    interstitialAd.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                this@ViewActivity.interstitialAd = null
                                Log.d("TAG", "The ad was dismissed.")
                                imageClass?.let { save(it, this@ViewActivity) }
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                // Called when fullscreen content failed to show.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                this@ViewActivity.interstitialAd = null
                                Log.d("TAG", "The ad failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                Log.d("TAG", "The ad was shown.")
                            }
                        }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                    interstitialAd = null
                    val error = String.format(
                        "domain: %s, code: %d, message: %s",
                        loadAdError.domain, loadAdError.code, loadAdError.message
                    )
//                    Toast.makeText(
//                        this@ViewActivity, "onAdFailedToLoad() with error: $error", Toast.LENGTH_SHORT
//                    )
//                        .show()
                    Log.e("interstial error", error)
                }
            })
    }

    private fun showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null) {
            interstitialAd!!.show(this)
        } else {
//            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
            imageClass?.let { save(it, this@ViewActivity) }
        }
    }

}

