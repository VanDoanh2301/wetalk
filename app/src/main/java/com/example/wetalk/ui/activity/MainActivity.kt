package com.example.wetalk.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.wetalk.R
import com.example.wetalk.ui.fragment.TalkHomeFragment
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
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
            val navController = navHostFragment.navController

            val currentDestination = navController.currentDestination
            when(currentDestination!!.id) {
                R.id.talkHomeFragment -> {
                    finish()
                }
                R.id.testResultFragment -> {
                    navController.navigate(R.id.action_testResultFragment_to_talkTopicFragment)
                }
                else -> {
                super.onBackPressed()
            }
            }

        }

    }
}