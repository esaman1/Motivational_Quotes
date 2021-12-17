package com.daily.motivational.quotes.activities

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.androidnetworking.AndroidNetworking
import com.daily.motivational.quotes.MyApplication
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.adapters.LibraryPagerAdapter
import com.daily.motivational.quotes.dataClass.ConnectionDetector
import com.daily.motivational.quotes.dataClass.SharedPreference
import com.daily.motivational.quotes.databinding.ActivityHomeBinding
import com.daily.motivational.quotes.fragments.CategoryFragment
import com.daily.motivational.quotes.fragments.FavouriteFragment
import com.daily.motivational.quotes.fragments.HomeFragment
import com.daily.motivational.quotes.fragments.SettingFragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class HomeActivity : BaseActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    var prevMenuItem: MenuItem? = null

    var mViewPagerAdapter: LibraryPagerAdapter? = null
    var cd: ConnectionDetector? = null
    var adView: AdView? = null
    var isInternetPresent = false

    companion object {
        var isInternetPresent = false
        var binding: ActivityHomeBinding? = null
    }

    override fun permissionGranted() {
//        binding?.animationView1?.isVisible=true
//        binding?.viewPager?.let { setupViewPager(it) }
        navigate()
    }

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
            binding?.animationView1?.isVisible = true
            binding?.viewPager?.let { setupViewPager(it) }
            return
        }

        // Show the app open ad.

        // Show the app open ad.
        application
            .showAdIfAvailable(
                this@HomeActivity,
                object : MyApplication.OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        binding?.animationView1?.isVisible = true
                        binding?.viewPager?.let { setupViewPager(it) }
                    }
                })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this@HomeActivity, R.layout.activity_home)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        MobileAds.initialize(
            this
        ) { }
        AndroidNetworking.initialize(this@HomeActivity)
        SharedPreference.setLogin(this@HomeActivity, true)
        cd = ConnectionDetector(this@HomeActivity)

        binding?.bottomNavigationView?.itemIconTintList = null
        binding?.bottomNavigationView?.setOnNavigationItemSelectedListener(this)
        mViewPagerAdapter = LibraryPagerAdapter(supportFragmentManager)
        binding?.viewPager?.adapter = mViewPagerAdapter
        binding?.viewPager?.offscreenPageLimit = 4

        binding?.viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(i: Int, v: Float, i1: Int) {}
            override fun onPageSelected(i: Int) {
                if (prevMenuItem != null) {
                    prevMenuItem?.isChecked = false
                } else {
                    binding?.bottomNavigationView?.menu?.getItem(0)?.isChecked = false
                }
                binding?.bottomNavigationView?.menu?.getItem(i)?.isChecked = true
                prevMenuItem = binding?.bottomNavigationView?.menu?.getItem(i)

                when (i) {
                    0 -> {
                        binding?.title?.text = getString(R.string.title)
                    }
                    1 -> {
                        binding?.title?.text = getString(R.string.category)
                    }
                    2 -> {
                        binding?.title?.text = getString(R.string.favourite)
                    }
                    3 -> {
                        binding?.title?.text = getString(R.string.setting)
                    }
                }
            }

            override fun onPageScrollStateChanged(i: Int) {}
        })

//        MobileAds.setRequestConfiguration(
//            RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("C83797C302AB5E5EDAE240B279B5946F"))
//                .build()
//        )


//        val adRequest = AdRequest.Builder (). build ()
//        binding?.adView?.loadAd (adRequest)
        cd = ConnectionDetector(applicationContext)
        isInternetPresent = cd!!.isConnectingToInternet()
        if (isInternetPresent) {
            val handler = Handler()
            handler.postDelayed({ // Step 1 - Create an AdView and set the ad unit ID on it.
                adView = AdView(this@HomeActivity)
                adView!!.adUnitId = getString(R.string.banner_ad_home)
                LoadSmartBanner()
                binding?.adView!!.addView(adView)
                adView!!.adListener = object : AdListener() {

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        super.onAdFailedToLoad(loadAdError)
//                        Toast.makeText(this@HomeActivity,"error:" + loadAdError.message,Toast.LENGTH_LONG).show()
//                        Log.e("error:",loadAdError.message.toString())
                    }
                }

            }, 2000)
        }
//        binding?.viewPager?.let { setupViewPager(it) }
    }

    fun LoadSmartBanner() {
        val adRequest = AdRequest.Builder().build()
        adView?.adSize = AdSize.SMART_BANNER
        adView?.loadAd(adRequest)
//        binding?.smartAd?.visibility = View.GONE
        adView?.adListener = object : AdListener() {

            override fun onAdLoaded() {
                super.onAdLoaded()
                binding?.adView?.visibility = View.VISIBLE
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> {
                binding?.viewPager?.currentItem = 0
                binding?.title?.text = getString(R.string.title)
            }
            R.id.category -> {
                binding?.viewPager?.currentItem = 1
                binding?.title?.text = getString(R.string.category)
            }
            R.id.favourite -> {
                binding?.viewPager?.currentItem = 2
                binding?.title?.text = getString(R.string.favourite)
            }
            R.id.setting -> {
                binding?.viewPager?.currentItem = 3
                binding?.title?.text = getString(R.string.setting)
            }
        }
        return false
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val adapter = LibraryPagerAdapter(supportFragmentManager)
        val mainFragment = HomeFragment()
        val categoryFragment = CategoryFragment()
        val favouriteFragment = FavouriteFragment()
        val settingFragment = SettingFragment()
        adapter.addFragment(mainFragment)
        adapter.addFragment(categoryFragment)
        adapter.addFragment(favouriteFragment)
        adapter.addFragment(settingFragment)
        viewPager.adapter = adapter
    }

    override fun onBackPressed() {
        if (binding?.viewPager?.currentItem != 0) {
            binding?.viewPager?.currentItem = 0
            binding?.title?.text = getString(R.string.title)
        } else {
            showExitDialog()
        }
    }

    var nativeView: View? = null
    private fun showExitDialog() {
        val alertadd = AlertDialog.Builder(this@HomeActivity)
        val factory = LayoutInflater.from(this@HomeActivity)
        nativeView = factory.inflate(R.layout.exit_popup, null)
        alertadd.setView(nativeView)
        loadNativeAd()
        alertadd.setMessage("Are you sure you want to exit?")
        alertadd.setPositiveButton(
            "Yes"
        ) { dialog, id -> finish() }
        alertadd.setNegativeButton(
            "No"
        ) { dialog, which -> dialog.dismiss() }
        alertadd.show()
    }

    private var nativeAd: NativeAd? = null
    private fun loadNativeAd() {
        val builder = AdLoader.Builder(this, R.string.native_ad.toString())
        builder.forNativeAd(
            OnNativeAdLoadedListener { nativeAd ->
                // OnLoadedListener implementation.
                // If this callback occurs after the activity is destroyed, you must call
                // destroy and return or you may get a memory leak.
                var isDestroyed = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    isDestroyed = isDestroyed()
                }
                if (isDestroyed || isFinishing || isChangingConfigurations) {
                    nativeAd.destroy()
                    return@OnNativeAdLoadedListener
                }
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (this@HomeActivity.nativeAd != null) {
                    this@HomeActivity.nativeAd!!.destroy()
                }
                this@HomeActivity.nativeAd = nativeAd
                val frameLayout = findViewById<FrameLayout>(R.id.fl_adplaceholder)
                val adView = layoutInflater.inflate(R.layout.ad_unified, null) as NativeAdView
                populateNativeAdView(nativeAd, adView)
                frameLayout.removeAllViews()
                frameLayout.addView(adView)
            })
        val videoOptions =
            VideoOptions.Builder().setStartMuted(false).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()
        builder.withNativeAdOptions(adOptions)
        val adLoader = builder
            .withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {

                        val error = String.format(
                            "domain: %s, code: %d, message: %s",
                            loadAdError.domain,
                            loadAdError.code,
                            loadAdError.message
                        )
                        Log.e("native error", error)

                    }
                })
            .build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onResume() {
        super.onResume()
        isInternetPresent = cd!!.isConnectingToInternet()
        Log.e("isInternetPresent", isInternetPresent.toString())
        if (isInternetPresent) {
            binding?.animationView?.isVisible = false
            binding?.noInternet?.isVisible = false
            binding?.mainRL?.isVisible = true
        } else {
            binding?.animationView1?.isVisible = false
            binding?.animationView?.isVisible = true
            binding?.noInternet?.isVisible = true
            binding?.mainRL?.isVisible = false
        }
    }

    private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and mediaContent are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }
        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.mediaContent.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {


            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
            }
        } else {

        }
    }
}