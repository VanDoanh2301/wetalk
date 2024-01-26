package com.example.wetalk.util.helper

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager.LayoutParams
import android.widget.PopupWindow
import com.example.wetalk.R

/**
 * The keyboard height provider, this class uses a PopupWindow
 * to calculate the window height when the floating keyboard is opened and closed.
 */
class KeyboardHeightProvider(private val activity: Activity) : PopupWindow(activity) {

    private var observer: KeyboardHeightObserver? = null

    private var keyboardLandscapeHeight = 0

    private var keyboardPortraitHeight = 0

    private val popupView: View

    private val parentView: View

    init {
        val inflator = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflator.inflate(R.layout.popupwindow, null, false)
        setContentView(popupView)

        softInputMode = LayoutParams.SOFT_INPUT_ADJUST_RESIZE or LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED

        parentView = activity.findViewById(android.R.id.content)

        width = 0
        height = LayoutParams.MATCH_PARENT

        popupView.viewTreeObserver.addOnGlobalLayoutListener {
            handleOnGlobalLayout()
        }
    }

    fun start() {
        if (!isShowing && parentView.windowToken != null) {
            setBackgroundDrawable(ColorDrawable(0))
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0)
        }
    }

    fun close() {
        observer = null
        dismiss()
    }

    fun setKeyboardHeightObserver(observer: KeyboardHeightObserver?) {
        this.observer = observer
    }

    private fun handleOnGlobalLayout() {
        val screenSize = Point()
        activity.windowManager.defaultDisplay.getSize(screenSize)

        val rect = Rect()
        popupView.getWindowVisibleDisplayFrame(rect)
        val orientation = getScreenOrientation()
        val keyboardHeight = screenSize.y - rect.bottom

        if (keyboardHeight == 0) {
            notifyKeyboardHeightChanged(0, orientation)
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            keyboardPortraitHeight = keyboardHeight
            notifyKeyboardHeightChanged(keyboardPortraitHeight, orientation)
        } else {
            keyboardLandscapeHeight = keyboardHeight
            notifyKeyboardHeightChanged(keyboardLandscapeHeight, orientation)
        }
    }

    private fun getScreenOrientation(): Int {
        return activity.resources.configuration.orientation
    }

    private fun notifyKeyboardHeightChanged(height: Int, orientation: Int) {
        observer?.onKeyboardHeightChanged(height, orientation)
    }
}

