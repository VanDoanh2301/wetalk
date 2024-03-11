package com.example.wetalk.util

import android.content.Context
import android.content.SharedPreferences


object SharedPreferencesUtils {
    private var mPref: SharedPreferences? = null
    const val NAME = "PROVIDE_CDL";

    fun init(context: Context) {
        mPref = context.getSharedPreferences(ACCESS_TOKEN, Context.MODE_PRIVATE)
    }
    fun setBoolean(_key: String?, _value: Boolean) {
        if (_key == null) {
            return
        }
        if (mPref != null) {
            val edit = mPref!!.edit()
            edit.putBoolean(_key, _value)
            edit.commit()
        }
    }

    fun getBoolean(_key: String?): Boolean {
        if (mPref == null || !mPref!!.contains(_key)) {
            val edit = mPref!!.edit()
            edit.putBoolean(_key, false)
            edit.commit()
        }
        return mPref!!.getBoolean(_key, false)
    }

    fun getBoolean(_key: String?, _value: Boolean): Boolean {
        if (mPref == null || !mPref!!.contains(_key)) {
            val edit = mPref!!.edit()
            edit.putBoolean(_key, _value)
            edit.commit()
        }
        return mPref!!.getBoolean(_key, _value)
    }

    fun setString(_key: String?, _value: String?) {
        if (_key == null) {
            return
        }
        if (mPref != null) {
            val edit = mPref!!.edit()
            edit.putString(_key, _value)
            edit.commit()
        }
    }

    fun getString(_key: String?): String? {
        if (mPref == null || !mPref!!.contains(_key)) {
            val edit = mPref!!.edit()
            edit.putString(_key, null)
            edit.commit()
        }
        return mPref!!.getString(_key, null)
    }

    fun getString(_key: String?, _value: String?): String? {
        if (mPref == null || !mPref!!.contains(_key)) {
            val edit = mPref!!.edit()
            edit.putString(_key, _value)
            edit.commit()
        }
        return mPref!!.getString(_key, _value)
    }

    fun setInt(_key: String?, _value: Int) {
        if (_key == null) {
            return
        }
        if (mPref != null) {
            val edit = mPref!!.edit()
            edit.putInt(_key, _value)
            edit.commit()
        }
    }

    fun getInt(_key: String?, _value: Int): Int {
        if (mPref == null || !mPref!!.contains(_key)) {
            val edit = mPref!!.edit()
            edit.putInt(_key, _value)
            edit.commit()
        }
        return mPref!!.getInt(_key, _value)
    }

    fun getInt(_key: String?): Int {
        return mPref!!.getInt(_key, -1)
    }

    fun setFloat(_key: String?, _value: Float) {
        if (_key == null) {
            return
        }
        if (mPref != null) {
            val edit = mPref!!.edit()
            edit.putFloat(_key, _value)
            edit.commit()
        }
    }

    fun getFloat(_key: String?, _value: Float): Float {
        if (mPref == null || !mPref!!.contains(_key)) {
            val edit = mPref!!.edit()
            edit.putFloat(_key, _value)
            edit.commit()
        }
        return mPref!!.getFloat(_key, _value)
    }

    fun saveToken(token: String) {
        mPref!!.edit().putString(ACCESS_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return mPref!!.getString(ACCESS_TOKEN, null)
    }

    fun clearToken() {
        mPref!!.edit().remove(ACCESS_TOKEN).apply()
    }

    fun saveRoom(room: String) {
        mPref!!.edit().putString(ROOM_CHAT, room).apply()
    }

    fun getRoom(): String? {
        return mPref!!.getString(ROOM_CHAT, null)
    }
    fun setCurrentUser(id:Int)  {
         mPref!!.edit().putInt(CURRENT_USER, id).apply()
    }
    fun getCurrentUser(): Int? {
        return mPref?.getInt(CURRENT_USER, 0) ?: 0
    }

    fun clearROOM() {
        mPref!!.edit().remove(ROOM_CHAT).apply()
    }

}