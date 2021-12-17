package com.daily.motivational.quotes.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.activities.InnerCategoryActivity
import com.daily.motivational.quotes.model.CategoryClass
import java.util.*

class CategoryAdapter(context: Context?) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    var context: Context? = null

    companion object {
        var mainArrayList = ArrayList<CategoryClass>()
        var arrayFilterList: ArrayList<CategoryClass> = ArrayList<CategoryClass>()
    }

    init {
        this.context = context
        mainArrayList.clear()
        arrayFilterList.clear()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        var itemView: View? = null
        var viewHolder: RecyclerView.ViewHolder? = null


        itemView =
            LayoutInflater.from(context)
                .inflate(R.layout.category_list, parent, false)
        viewHolder = MyClassView(itemView)


        return viewHolder
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holders: RecyclerView.ViewHolder, position: Int) {
        val options = RequestOptions()

        var categoryClass = arrayFilterList.get(position)


        val holder = holders as MyClassView
        holder.mImage.adjustViewBounds = true
        holder.mImage.clipToOutline = true
        Glide.with(context!!)
            .load(categoryClass.url)
            .apply(
                options.centerCrop()
                    .skipMemoryCache(true)
                    .priority(Priority.LOW)
                    .format(DecodeFormat.PREFER_ARGB_8888)
            )
            .into(holder.mImage)


        holder.mMainRL.setOnClickListener {
            val intent = Intent(context, InnerCategoryActivity::class.java)
            intent.putExtra("cid", categoryClass.id)
            intent.putExtra("cname", categoryClass.name)
            context?.startActivity(intent)
        }

        holder.mTitle.text = categoryClass.name

    }

    override fun getFilter(): Filter? {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    arrayFilterList = mainArrayList
                } else {
                    val filteredList1: ArrayList<CategoryClass> = ArrayList<CategoryClass>()
                    for (row in mainArrayList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.name?.lowercase()?.contains(charString.lowercase()) == true) {
                            filteredList1.add(row)
                        }
                    }
                    arrayFilterList = filteredList1
                }
                val filterResults = FilterResults()
                filterResults.values = arrayFilterList
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                arrayFilterList = filterResults.values as ArrayList<CategoryClass>

                // refresh the list with filtered data
                notifyDataSetChanged()
            }
        }
    }


    override fun getItemCount(): Int {
        return arrayFilterList.size
    }


    class MyClassView(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mImage: ImageView
        var mTitle: TextView
        var mMainRL: RelativeLayout

        init {
            mImage = itemView.findViewById(R.id.item_image)
            mTitle = itemView.findViewById(R.id.mTitle)
            mMainRL = itemView.findViewById(R.id.catRL)
        }
    }

    fun addAll(model: CategoryClass?) {
        if (model != null) {
            mainArrayList.add(model)
//            arrayFilterList.add(model)
        }
        notifyDataSetChanged()
    }

    fun newArray() {
        mainArrayList = ArrayList<CategoryClass>()
        arrayFilterList = ArrayList<CategoryClass>()
        mainArrayList.clear()
        arrayFilterList.clear()
        notifyDataSetChanged()
    }

}