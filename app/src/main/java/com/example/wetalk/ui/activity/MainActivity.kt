package com.example.wetalk.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.wetalk.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    override fun onBackPressed() {
        val index = supportFragmentManager.backStackEntryCount
        if (index > 0) {
            /** get fragment in stack and pop */
            val backEntry = supportFragmentManager.getBackStackEntryAt(index - 1)
            val tag = backEntry.name
            supportFragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        } else {
            super.onBackPressed()
        }
    }
}