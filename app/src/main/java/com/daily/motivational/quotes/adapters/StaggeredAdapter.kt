package com.daily.motivational.quotes.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.activities.ViewActivity
import com.daily.motivational.quotes.dataClass.HomeData.Companion.mainArrayList
import com.daily.motivational.quotes.model.ImageClass
import java.util.*


class StaggeredAdapter(context1: Context?) :
    RecyclerView.Adapter<StaggeredAdapter.MyClassView>() {
    var context: Context? = null
    var type: Int = 0

    init {
        this.context = context1
        mainArrayList.clear()
    }

    private val inflater: LayoutInflater = LayoutInflater.from(context1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClassView {
        val itemView = inflater.inflate(R.layout.item_list, parent, false)
        return MyClassView(itemView)
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyClassView, position: Int) {
        val options = RequestOptions()

        var imageClass = mainArrayList.get(position)

        Glide.with(context!!)
            .load(imageClass.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(context!!.resources.getDrawable(R.drawable.image_bg))
            .into(holder.mImage)

//        try {
//            Picasso.with(context).load(imageClass.image)
//                .into(holder.mImage)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        holder.mImage.setOnClickListener {
            val intent = Intent(context, ViewActivity::class.java)
            val args = Bundle()
            args.putSerializable("modelArray", mainArrayList)
            args.putInt("position", position)
            intent.putExtra("BUNDLE", args)
            context?.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return mainArrayList.size
    }

    class MyClassView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImage: ImageView

        init {
            mImage = itemView.findViewById(R.id.item_image)
        }
    }


    fun add(model: ImageClass?) {
        if (model != null) {
            mainArrayList.add(model)
        }
        notifyItemRangeInserted(itemCount, mainArrayList.size - 1)
    }

    fun newArray(type: Int) {
        mainArrayList = ArrayList<ImageClass>()
        this.type = type
        notifyDataSetChanged()
    }

}