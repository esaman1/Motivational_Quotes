package com.daily.motivational.quotes.adapters

import android.app.Activity
import android.content.SharedPreferences
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.activities.ViewActivity
import com.daily.motivational.quotes.activities.ViewActivity.Companion.currentPosition
import com.daily.motivational.quotes.activities.ViewFavouritesActivity
import com.daily.motivational.quotes.dataClass.HomeData.Companion.collapse
import com.daily.motivational.quotes.dataClass.HomeData.Companion.expand
import com.daily.motivational.quotes.model.ImageClass
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ShowImageAdapter(var activity: Activity) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var json1: String? = null
    var from: String? = null
    var sharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    var mFavouriteImageList = ArrayList<ImageClass>()
    var gson: Gson? = null
    var mActivity: Activity? = null

    companion object {
        var mArrayList = java.util.ArrayList<ImageClass>()
    }

    init {
        this.mActivity = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemView: View
        var viewHolder: RecyclerView.ViewHolder? = null
        itemView =
            LayoutInflater.from(activity).inflate(R.layout.expand_image_layout, parent, false)
        viewHolder = MyClassView(itemView)

        return viewHolder
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holders: RecyclerView.ViewHolder, position: Int) {
        val options = RequestOptions()
        var imgClass = mArrayList[position]
        val holder = holders as MyClassView

        holder.mImage.clipToOutline = true
        holder.mImage.adjustViewBounds = true
        Glide.with(activity)
            .load(imgClass.image)
            .apply(
                options.skipMemoryCache(true)
                    .priority(Priority.LOW)
                    .format(DecodeFormat.PREFER_ARGB_8888)
            )
            .into(holder.mImage)

        holder.mImage.setOnClickListener {
            Log.e("position", position.toString())
            currentPosition = position
            if (from.equals("HOME")) {
                if (ViewActivity.binding?.mBottomLL?.isVisible == true) {
                    collapse(ViewActivity.binding?.mBottomLL!!)
                } else {
                    mArrayList.get(position).id?.let { it1 -> checkfavourites(it1) }
                    expand(ViewActivity.binding?.mBottomLL!!)
                }
            } else {
                if (ViewFavouritesActivity.binding?.mBottomLL?.isVisible == true) {
                    collapse(ViewFavouritesActivity.binding?.mBottomLL!!)
                } else {
                    mArrayList.get(position).id?.let { it1 -> checkfavourites(it1) }
                    expand(ViewFavouritesActivity.binding?.mBottomLL!!)
                }
            }
        }


    }

    fun checkfavourites(pos: String) {
        sharedPreferences = mActivity?.getSharedPreferences(
            "Favourites_pref",
            AppCompatActivity.MODE_PRIVATE
        )
        editor = sharedPreferences?.edit()
        try {
            gson = Gson()
            val json = sharedPreferences?.getString("Fav_Image", "")
            val type = object : TypeToken<ArrayList<ImageClass?>?>() {}.type
            mFavouriteImageList = gson?.fromJson(json, type)!!

            if (mFavouriteImageList.size > 0) {
                if (from.equals("HOME")) {
                    for (i in mFavouriteImageList.indices) {
                        if ((mFavouriteImageList.get(i).id).equals(pos)) {
                            ViewActivity.binding?.mFavourite?.setImageDrawable(
                                mActivity?.resources?.getDrawable(
                                    R.drawable.ic_liked
                                )
                            )
                            break
                        } else {
                            ViewActivity.binding?.mFavourite?.setImageDrawable(
                                mActivity?.resources?.getDrawable(
                                    R.drawable.ic_fav
                                )
                            )
                        }
                    }
                } else {
                    for (i in mFavouriteImageList.indices) {
                        if ((mFavouriteImageList.get(i).id).equals(pos)) {
                            ViewFavouritesActivity.binding?.mFavourite?.setImageDrawable(
                                mActivity?.resources?.getDrawable(
                                    R.drawable.ic_liked
                                )
                            )
                            break
                        } else {
                            ViewFavouritesActivity.binding?.mFavourite?.setImageDrawable(
                                mActivity?.resources?.getDrawable(
                                    R.drawable.ic_fav
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.message?.let { Log.e("error", it) }
        }
    }

    override fun getItemCount(): Int {
        return mArrayList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    class MyClassView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImage: ImageView

        init {
            mImage = itemView.findViewById(R.id.mImage)
        }
    }

    fun add(model: ImageClass?) {
        if (model != null) {
            mArrayList.add(model)
        }
        notifyDataSetChanged()
    }

    fun newArray(from: String) {
        mArrayList = ArrayList<ImageClass>()
        this.from = from
        notifyDataSetChanged()
    }


}