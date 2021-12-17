package com.daily.motivational.quotes.fragments

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CompoundButton
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.airbnb.lottie.LottieAnimationView
import com.daily.motivational.quotes.BuildConfig
import com.daily.motivational.quotes.R
import com.daily.motivational.quotes.activities.PolicyActivity
import com.daily.motivational.quotes.dataClass.SharedPreference
import com.daily.motivational.quotes.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    var mActivity: Activity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_setting, container, false
            )
        view1 = binding?.root
        mActivity = requireActivity()

        binding?.mShare?.setOnClickListener {
            try {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name))
                var shareMessage = "\nLet me recommend you this application\n\n"
                shareMessage =
                    """
        ${shareMessage}https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}        
        
        """.trimIndent()
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
                startActivity(Intent.createChooser(shareIntent, "Share via"))
            } catch (e: Exception) {
                //e.toString();
            }
        }

        var noti = SharedPreference.getNotificationStatus(mActivity as FragmentActivity)
        binding?.switchCompress?.isChecked = noti

        binding?.mRate?.setOnClickListener {
            showRateDialog()
        }

        binding?.mContact?.setOnClickListener {
            sendFeedback()
        }

        binding?.mPolicy?.setOnClickListener {
            val intent = Intent(activity, PolicyActivity::class.java)
            startActivity(intent)
        }

        binding?.switchCompress?.setOnCheckedChangeListener(object :
            CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (isChecked) {
                    SharedPreference.setNotificationStatus(mActivity as FragmentActivity, true)
                } else {
                    SharedPreference.setNotificationStatus(mActivity as FragmentActivity, false)
                }
            }
        })

        return view1
    }

    fun sendFeedback() {
        val emailIntent = Intent(
            Intent.ACTION_SENDTO,
            Uri.parse("mailto:" + mActivity?.resources?.getString(R.string.feedback_email))
        )
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback")
        try {
            startActivity(Intent.createChooser(emailIntent, "Send email via..."))
        } catch (ex: ActivityNotFoundException) {
            Toast.makeText(
                requireActivity().applicationContext,
                "There are no email clients installed.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showRateDialog() {
        val dialog = Dialog(requireActivity(), android.R.style.Theme_Black_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialige_rate_us)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0))
        dialog.setCanceledOnTouchOutside(true)
        val animationView = dialog.findViewById(R.id.animationView) as LottieAnimationView
        animationView.animate()
        val yesBtn = dialog.findViewById(R.id.yes) as TextView
        val noBtn = dialog.findViewById(R.id.no) as TextView
        yesBtn.setOnClickListener {
            val appPackageName =
                requireActivity().packageName

            try {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$appPackageName")
                    )
                )
            } catch (anfe: ActivityNotFoundException) {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                    )
                )
            }
            dialog.dismiss()
        }
        noBtn.setOnClickListener { dialog.dismiss() }
        dialog.show()

    }

    companion object {
        var binding: FragmentSettingBinding? = null
        var view1: View? = null
    }
}