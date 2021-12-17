package com.daily.motivational.quotes.fragments

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.activities.HomeActivity
import com.daily.motivational.quotes.adapters.StaggeredAdapter
import com.daily.motivational.quotes.dataClass.HomeData.Companion.pageCount
import com.daily.motivational.quotes.databinding.FragmentHomeBinding
import com.daily.motivational.quotes.model.ImageClass
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException


class HomeFragment : Fragment() {

    var binding: FragmentHomeBinding? = null
    lateinit var popularModel: ImageClass
    var visibleItemCount = 0
    var pastVisiblesItems: Int = 0
    var totalItemCount: Int = 0
    var lastPositions = IntArray(0)
    var adapter: StaggeredAdapter? = null
    var mActivity: Activity? = null
    var view1: View? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_home, container, false
            )
        view1 = binding?.root
        mActivity = requireActivity()
        var staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        staggeredGridLayoutManager.gapStrategy =
            StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding?.recyclerView?.layoutManager = staggeredGridLayoutManager
        adapter = StaggeredAdapter(mActivity)
        binding?.recyclerView?.adapter = adapter
        adapter?.newArray(0)

        var firstCall = false
        binding?.scrollView?.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight &&
                    scrollY > oldScrollY
                ) {

                    visibleItemCount = staggeredGridLayoutManager.childCount
                    totalItemCount = staggeredGridLayoutManager.itemCount

                    if (!firstCall) {
                        lastPositions = IntArray(staggeredGridLayoutManager.spanCount)
                        lastPositions =
                            staggeredGridLayoutManager.findLastCompletelyVisibleItemPositions(
                                lastPositions
                            )
                        pastVisiblesItems = max(lastPositions[0], lastPositions[1])
                        firstCall = true
                    }

                    Log.e(
                        "LLLLL_count: ",
                        "Visible: " + visibleItemCount + " totalItem: " + totalItemCount +
                                " pastVisible: " + pastVisiblesItems
                    )
                    if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                        if (pageCount != -1) showProgressView()
                        getData()
                    }
                }
            }
        })

        getData()
        return view1
    }


    fun max(a: Int, b: Int): Int {
        Log.e("a :$a", "b : $b")
        return if (a >= b) a else b
    }

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
                        Log.e("Home response :- ", response.toString())
                        Toast.makeText(activity, "No More Data Here!!!", Toast.LENGTH_SHORT).show()
                    } else {
                        pageCount++
                        Log.e("Home response :- ", response.toString())
                        Log.e("Home length :- ", response.length().toString())
                        for (i in 0 until response.length()) {
                            try {
                                val `object` = response.getJSONObject(i)
                                popularModel =
                                    Gson().fromJson(`object`.toString(), ImageClass::class.java)
                                adapter?.add(popularModel)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                        HomeActivity.binding?.animationView1?.isVisible = false
                    }
                }

                override fun onError(error: ANError) {
                    hideProgressView()
//                    Log.e("Home error:- ", error.message.toString())
                    if (error.errorDetail.equals("connectionError")) {
                        mActivity?.let {
                            Toasty.info(it, "No Internet Found!!!", Toast.LENGTH_SHORT).show()
                        }
                    } else if (error.errorDetail.equals("parseError")) {
                        mActivity?.let {
                            Toasty.info(it, "No More Data Here!!!", Toast.LENGTH_SHORT).show()
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