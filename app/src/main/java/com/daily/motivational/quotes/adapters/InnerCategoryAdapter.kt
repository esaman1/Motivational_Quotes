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
import com.daily.motivational.quotes.activities.ViewCategoryDataActivity
import com.daily.motivational.quotes.dataClass.HomeData
import com.daily.motivational.quotes.model.ImageClass

class InnerCategoryAdapter(context1: Context?) :
    RecyclerView.Adapter<InnerCategoryAdapter.MyClassView>() {
    var context: Context? = null
    var category: String? = null
    var cid: String? = null

    companion object {
        var mArrayList = ArrayList<ImageClass>()
    }

    init {
        this.context = context1
        mArrayList.clear()

    }

    private val inflater: LayoutInflater = LayoutInflater.from(context1)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClassView {
        val itemView = inflater.inflate(R.layout.item_list, parent, false)
        return MyClassView(itemView)
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: MyClassView, position: Int) {
        val options = RequestOptions()

        var imageClass = mArrayList.get(position)

        Glide.with(context!!)
            .load(imageClass.image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(context!!.resources.getDrawable(R.drawable.image_bg))
            .into(holder.mImage)

//        try {
//            Picasso.with(context).load(imageClass.image).into(holder.mImage)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }

        holder.mImage.setOnClickListener {
            val intent = Intent(context, ViewCategoryDataActivity::class.java)
            val args = Bundle()
            args.putSerializable("modelArray", mArrayList)
            args.putInt("position", position)
            args.putString("cid", cid)
            args.putString("cname", category)
            intent.putExtra("BUNDLE", args)
            context?.startActivity(intent)
        }


    }


    override fun getItemCount(): Int {
        return mArrayList.size
    }

    class MyClassView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImage: ImageView

        init {
            mImage = itemView.findViewById(R.id.item_image)
        }
    }


    fun addAll(model: ImageClass?) {
        if (model != null) {
            mArrayList.add(model)
        }
        notifyItemRangeInserted(itemCount, HomeData.mainArrayList.size - 1)
    }

    fun newArray(type: String, cid: String) {
        mArrayList = ArrayList<ImageClass>()
        this.category = type
        this.cid = cid
        notifyDataSetChanged()
    }

}