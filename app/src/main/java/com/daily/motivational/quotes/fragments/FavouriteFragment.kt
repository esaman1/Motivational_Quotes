package com.daily.motivational.quotes.fragments

import android.content.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.adapters.FavouritesAdapter
import com.daily.motivational.quotes.databinding.FragmentFavouriteBinding
import com.daily.motivational.quotes.model.ImageClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class FavouriteFragment : Fragment() {

    private var refreshReceiver: RefreshReceiver? = null
    var staggeredGridLayoutManager: StaggeredGridLayoutManager? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_favourite, container, false
            )
        view1 = binding?.root
        mActivity = requireActivity()

        adapter = FavouritesAdapter(mActivity)
        staggeredGridLayoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding?.recyclerView?.layoutManager = staggeredGridLayoutManager
        binding?.recyclerView?.adapter = adapter
        adapter?.newArray()

        refreshReceiver = RefreshReceiver()
        LocalBroadcastManager.getInstance(requireActivity().baseContext).registerReceiver(
            refreshReceiver!!,
            IntentFilter("REFRESH")
        )

        return view1
    }

    override fun onResume() {
        super.onResume()
        setAdapter()
    }

    companion object {
        var binding: FragmentFavouriteBinding? = null
        var view1: View? = null
        var mFavouriteImageList = ArrayList<ImageClass>()
        var json1: String? = null
        var sharedPreferences: SharedPreferences? = null
        var gson: Gson? = null
        var mActivity: FragmentActivity? = null
        var adapter: FavouritesAdapter? = null

        fun setAdapter() {
            adapter?.newArray()
            mFavouriteImageList = ArrayList()
            sharedPreferences =
                mActivity?.getSharedPreferences("Favourites_pref", Context.MODE_PRIVATE)
            gson = Gson()
            json1 = sharedPreferences!!.getString("Fav_Image", "")
            val type1 = object : TypeToken<ArrayList<ImageClass?>?>() {}.type
            try {
                mFavouriteImageList = gson!!.fromJson(json1, type1)
//                Log.e("Fav size", mFavouriteImageList.size.toString())
                if (mFavouriteImageList.size > 0) {
                    binding?.recyclerView?.isVisible = true
                    binding?.noData?.isVisible = false
                    for (i in mFavouriteImageList.indices) {
                        adapter?.add(mFavouriteImageList[i])
                    }
                    adapter?.notifyDataSetChanged()
                } else {
                    binding?.recyclerView?.isVisible = false
                    binding?.noData?.isVisible = true
                }
            } catch (e: Exception) {
                binding?.recyclerView?.isVisible = false
                binding?.noData?.isVisible = true
                Log.e("error", e.message.toString())
            }
        }
    }

    private class RefreshReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            setAdapter()
        }
    }
}