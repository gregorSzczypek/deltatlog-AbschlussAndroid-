package com.example.deltatlog.util

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import com.example.deltatlog.databinding.FragmentProjectBinding

class ProjectFragmentAnimator {

    // provides an instance of the swell and rotate animation and manages the hint text visibility
    fun animateFAB(isEmpty: Boolean, binding: FragmentProjectBinding) {
        val fab = binding.floatingActionButton

        // Scale animation
        if (isEmpty) {
            // Create scale animation
            val scaleAnimation = ScaleAnimation(
                1.0f, 1.5f, 1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
            ).apply {
                duration = 1000
                repeatCount = Animation.INFINITE
                repeatMode = Animation.REVERSE
            }

            // Rotate animation
            val rotate = RotateAnimation(
                0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            ).apply {
                duration = 2000
                repeatCount = Animation.INFINITE
                repeatMode = Animation.RESTART
                interpolator = AccelerateDecelerateInterpolator()
            }

            // Create an animation set and add scale and rotate animations to it
            val animationSet = AnimationSet(true).apply {
                addAnimation(scaleAnimation)
                addAnimation(rotate)
            }
            // start the animation set
            fab.startAnimation(animationSet)

        } else {
            // clear the animation
            fab.clearAnimation()
        }
    }
}