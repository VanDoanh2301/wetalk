package com.example.wetalk.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wetalk.R
import com.example.wetalk.ui.activity.MainActivity
import com.rey.material.app.DialogFragment


abstract class BaseDialogFragment : DialogFragment() {
    protected var activity: MainActivity? = null
    protected var isCreated = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentActivity: Activity? = getActivity()
        if (currentActivity is MainActivity) {
            activity = currentActivity as MainActivity?
        }

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isCreated = true
        return inflater.inflate(layoutId, container, false)
    }
    protected abstract val layoutId: Int
    companion object {
        fun add(activity: AppCompatActivity?, fragment: Fragment) {
            if (activity != null && activity.supportFragmentManager != null) {
                if (activity.supportFragmentManager.findFragmentByTag(fragment.javaClass.name) == null) {
                    addFragmentWith(activity, fragment, fragment.javaClass.name)
                }
            }
        }
        private fun addFragmentWith(
            activity: AppCompatActivity, fragment: Fragment,
            tag: String
        ) {
            activity.supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(
                    R.anim.anim_in,
                    R.anim.anim_out,
                    R.anim.anim_in,
                    R.anim.anim_out
                )
                .addToBackStack(tag)
                .add(R.id.main, fragment, tag)
                .commitAllowingStateLoss()
        }
    }
}