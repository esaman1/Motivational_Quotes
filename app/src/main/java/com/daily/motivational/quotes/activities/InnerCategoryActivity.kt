package com.daily.motivational.quotes.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.adapters.InnerCategoryAdapter
import com.daily.motivational.quotes.databinding.ActivityInnerCategoryBinding
import com.daily.motivational.quotes.model.ImageClass
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException

class InnerCategoryActivity : AppCompatActivity() {
    var binding: ActivityInnerCategoryBinding? = null
    var cid: String? = null
    var cname: String? = null
    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
    var popularModel: ImageClass? = null
    var adapter: InnerCategoryAdapter? = null

    var visibleItemCount = 0
    var pastVisiblesItems: Int = 0
    var totalItemCount: Int = 0
    lateinit var lastPositions: IntArray

    companion object {
        var pageCount = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this@InnerCategoryActivity,
            R.layout.activity_inner_category
        )

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        binding?.mBack?.setOnClickListener { onBackPressed() }

        cid = intent.getStringExtra("cid")
        cname = intent.getStringExtra("cname")
        if (cid != null) {
//            cid?.let { Log.e(" cat cid inner : ", it) }
        }
        binding?.title?.text = cname

        adapter = InnerCategoryAdapter(this@InnerCategoryActivity)
        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding?.recyclerView?.layoutManager = staggeredGridLayoutManager
        binding?.recyclerView?.adapter = adapter
        cname?.let { cid?.let { it1 -> adapter?.newArray(it, it1) } }

        binding?.scrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight &&
                    scrollY > oldScrollY
                ) {
                    visibleItemCount = staggeredGridLayoutManager?.childCount!!
                    totalItemCount = staggeredGridLayoutManager?.itemCount!!

                    lastPositions = IntArray(staggeredGridLayoutManager!!.spanCount)
                    lastPositions =
                        staggeredGridLayoutManager!!.findLastCompletelyVisibleItemPositions(
                            lastPositions
                        )
                    pastVisiblesItems = Math.max(lastPositions.get(0), lastPositions.get(1))
                    Log.e(
                        "LLLLL_count: ",
                        "Visible: " + visibleItemCount + " totalItem: " + totalItemCount +
                                " pastVisible: " + pastVisiblesItems
                    )
                    if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                        if (pageCount != -1) showProgressView()
                        cid?.let { it1 -> getInnerData(it1) }
                    }
                }
            }
        })

        cid?.let { it1 -> getInnerData(it1) }
    }

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
//                    Log.e("cat response :- ", response.toString())
                    if (response.equals(" ")) {
                        pageCount = -1
//                        Toast.makeText(
//                            this@InnerCategoryActivity,
//                            "No More Data Here!!!",
//                            Toast.LENGTH_SHORT
//                        ).show()
                    } else {
                        pageCount++
                        for (i in 0 until response.length()) {
                            try {
                                val `object` = response.getJSONObject(i)
                                popularModel =
                                    Gson().fromJson(`object`.toString(), ImageClass::class.java)
                                adapter?.addAll(popularModel)
                            } catch (e: JSONException) {
//                                Log.e("cat error1:- ", e.message.toString())
                                e.printStackTrace()
                            }
                        }
                    }
                }

                override fun onError(error: ANError) {
                    hideProgressView()

//                    Log.e("cat error:- ", error.errorDetail.toString() )
                    if (error.errorDetail.equals("connectionError")) {
                        Toasty.info(
                            this@InnerCategoryActivity,
                            "No Internet Found!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (error.errorDetail.equals("parseError")) {
                        Toasty.info(
                            this@InnerCategoryActivity,
                            "No More Data Here!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toasty.info(
                            this@InnerCategoryActivity,
                            "Something went wrong!!!",
                            Toast.LENGTH_SHORT
                        ).show()
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

    override fun onBackPressed() {
        pageCount = 1
        super.onBackPressed()
    }

}