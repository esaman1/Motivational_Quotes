package com.daily.motivational.quotes.fragments

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.adapters.CategoryAdapter
import com.daily.motivational.quotes.databinding.FragmentCategoryBinding
import com.daily.motivational.quotes.databinding.FragmentHomeBinding
import com.daily.motivational.quotes.model.CategoryClass
import com.google.gson.Gson
import es.dmoral.toasty.Toasty
import org.json.JSONArray
import org.json.JSONException

class CategoryFragment : Fragment() {

    var binding: FragmentCategoryBinding? = null
    var adapter: CategoryAdapter? = null
    var popularModel: CategoryClass? = null
    var mActivity: Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_category, container, false
            )
        view1 = binding?.root
        mActivity = requireActivity()
        adapter = CategoryAdapter(requireContext())
        binding?.recyclerView?.layoutManager = GridLayoutManager(requireActivity(), 2)
        binding?.recyclerView?.adapter = adapter
        adapter?.newArray()

        binding?.searchBar?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                adapter?.filter?.filter(s)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        getCategory()
        return view1
    }

    fun getCategory() {

        AndroidNetworking.post("https://songskalyrics.com/app/quotesinhindi/admin/fetch_categories.php")
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    if (response.toString().trim { it <= ' ' } == "[]") {
                        Toast.makeText(activity, "No More Data Here!!!", Toast.LENGTH_SHORT).show()
                    } else {
                        CategoryAdapter.mainArrayList.clear()
                        CategoryAdapter.arrayFilterList.clear()
//                        Log.e("Home response :- ", response.toString())
                        for (i in 0 until response.length()) {
                            try {
                                val `object` = response.getJSONObject(i)
                                popularModel =
                                    Gson().fromJson(`object`.toString(), CategoryClass::class.java)
                                adapter?.addAll(popularModel)
                            } catch (e: JSONException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

                override fun onError(error: ANError) {
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
                    Log.e("Home error:- ", error.message.toString())
                }
            })
    }

    companion object {
        var binding: FragmentHomeBinding? = null
        var view1: View? = null
    }
}