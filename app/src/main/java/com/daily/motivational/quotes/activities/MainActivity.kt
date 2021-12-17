package com.daily.motivational.quotes.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.daily.motivational.quotes.MyApplication
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.adapters.WelcomeScreenAdapter
import com.daily.motivational.quotes.dataClass.SharedPreference
import com.daily.motivational.quotes.databinding.ActivityMainBinding
import com.daily.motivational.quotes.fragments.FragmentSplash1
import com.daily.motivational.quotes.fragments.FragmentSplash2
import com.daily.motivational.quotes.fragments.FragmentSplash3
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    var binding: ActivityMainBinding? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)

        if (SharedPreference.getLogin(this@MainActivity)) {
            oldLogin()
        } else {
            binding?.nextBtn?.visibility = View.VISIBLE
            binding?.skipBtn?.visibility = View.VISIBLE
            binding?.viewPager?.visibility = View.VISIBLE
            setViewPager()
        }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
//        navigate()
    }

    private fun setViewPager() {
        val adapter = WelcomeScreenAdapter(supportFragmentManager)
        adapter.addFragment(FragmentSplash1(), "FIRST_FRAGMENT")
        adapter.addFragment(FragmentSplash2(), "SECOND_FRAGMENT")
        adapter.addFragment(FragmentSplash3(), "THIRD_FRAGMENT")
        binding?.nextBtn?.setOnClickListener(this)
        binding?.skipBtn?.setOnClickListener(this)
        binding?.viewPager?.adapter = adapter
        binding?.viewPager?.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.next_btn ->
                if (binding?.viewPager?.currentItem == 0) {
                    binding?.viewPager?.currentItem = 1

                } else if (binding?.viewPager?.currentItem == 1) {
                    binding?.viewPager?.currentItem = 2
                } else if (binding?.viewPager?.currentItem == 2) {
                    newLogin()
                }

            R.id.skip_btn -> {
                newLogin()
            }
        }
    }


    var android_id: String = ""
    fun navigate() {

        val testDeviceIds = Arrays.asList("C83797C302AB5E5EDAE240B279B5946F")
        val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)

//        AdRequest.

        val application = application

        if (application !is MyApplication) {
            Log.e(
                "App error",
                "Failed to cast application to MyApplication."
            )
            if (SharedPreference.getLogin(this@MainActivity)) {
                oldLogin()
            } else {
                binding?.nextBtn?.visibility = View.VISIBLE
                binding?.skipBtn?.visibility = View.VISIBLE
                binding?.viewPager?.visibility = View.VISIBLE
                setViewPager()
            }
            return
        }

        // Show the app open ad.

        // Show the app open ad.
        application
            .showAdIfAvailable(
                this@MainActivity,
                object : MyApplication.OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        if (SharedPreference.getLogin(this@MainActivity)) {
                            oldLogin()
                        } else {
                            binding?.nextBtn?.visibility = View.VISIBLE
                            binding?.skipBtn?.visibility = View.VISIBLE
                            binding?.viewPager?.visibility = View.VISIBLE
                            setViewPager()
                        }
                    }
                })
    }

    fun newLogin() {
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        intent.putExtra("Login", "new")
        startActivity(intent)
        finish()
    }

    fun oldLogin() {
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        intent.putExtra("Login", "old")
        startActivity(intent)
        finish()
    }
}

