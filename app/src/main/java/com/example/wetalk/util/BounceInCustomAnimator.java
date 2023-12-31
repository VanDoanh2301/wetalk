package com.example.wetalk.util;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by duy on 10/25/17.
 */

public class BounceInCustomAnimator  extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0, 1, 1, 1),
                ObjectAnimator.ofFloat(target, "scaleX", 1f, 1.02f, 0.98f, 1),
                ObjectAnimator.ofFloat(target, "scaleY", 1f, 1.05f, 0.9f, 1)
        );
    }
}