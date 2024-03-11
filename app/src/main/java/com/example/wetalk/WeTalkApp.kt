package com.example.wetalk

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.example.wetalk.util.SharedPreferencesUtils
import com.example.wetalk.util.Task
import dagger.hilt.android.HiltAndroidApp
import java.util.Calendar

@HiltAndroidApp
class WeTalkApp : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private var currentActivity: Activity? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        this.registerActivityLifecycleCallbacks(this)
        app = this
        SharedPreferencesUtils.init(this)
        w = WeTalkApp.get().resources.displayMetrics.widthPixels
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }



    /** ActivityLifecycleCallback methods. */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}


    override fun onActivityDestroyed(activity: Activity) {

    }
    companion object {
        const val TEST_DEVICE_ID = "37D8EC79FC153EF8A739BF97D9E035B6"
        private lateinit var app: WeTalkApp
        private var w: Int = 0

        fun get(): WeTalkApp {
            return app
        }

        fun W(): Int {
            return w
        }

        fun showDatePicker(activity: AppCompatActivity, timeCallback: Task<Long>) {
            val calendar = Calendar.getInstance()

            val datePickerDialog = DatePickerDialog(activity, { _: DatePicker?, i: Int, i1: Int, i2: Int ->
                calendar.set(Calendar.YEAR, i)
                calendar.set(Calendar.MONTH, i1)
                calendar.set(Calendar.DAY_OF_MONTH, i2)
                timeCallback.callback(calendar.timeInMillis)
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH])

            val datePicker = datePickerDialog.datePicker
            datePickerDialog.show()
        }
    }


}
