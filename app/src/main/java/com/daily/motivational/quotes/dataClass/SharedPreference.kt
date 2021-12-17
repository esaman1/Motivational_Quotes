package com.daily.motivational.quotes.dataClass

import android.content.Context

class SharedPreference {

    companion object {
        val MyPREFERENCES = "SPData"
        var LOGIN = "LOGIN"
        var NOTIFICATION = "NOTIFICATION"

        fun getLogin(c1: Context): Boolean {
            val sharedpreferences = c1.getSharedPreferences(
                MyPREFERENCES,
                Context.MODE_PRIVATE
            )
            return sharedpreferences.getBoolean(LOGIN, false)
        }

        fun setLogin(c1: Context, value: Boolean) {
            val sharedpreferences =
                c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedpreferences.edit()
            editor.putBoolean(LOGIN, value)
            editor.apply()
        }

        fun getNotificationStatus(c1: Context): Boolean {
            val sharedpreferences = c1.getSharedPreferences(
                MyPREFERENCES,
                Context.MODE_PRIVATE
            )
            return sharedpreferences.getBoolean(NOTIFICATION, true)
        }

        fun setNotificationStatus(c1: Context, value: Boolean) {
            val sharedpreferences =
                c1.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE)
            val editor = sharedpreferences.edit()
            editor.putBoolean(NOTIFICATION, value)
            editor.apply()
        }
    }
}