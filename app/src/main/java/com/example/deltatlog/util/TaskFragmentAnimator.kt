package com.example.deltatlog.util

import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import com.example.deltatlog.databinding.FragmentTaskBinding

class TaskFragmentAnimator {

    // provides an instance of the swell and rotate animation and manages the hint text visibility
    fun animateFAB(isEmpty: Boolean, binding: FragmentTaskBinding) {
        val fab = binding.floatingActionButton

        // Scale animation
        if (isEmpty) {
            val scaleAnimation = ScaleAnimation(
                1.0f, 1.2f, 1.0f, 1.2f,
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

            val animationSet = AnimationSet(true).apply {
                addAnimation(scaleAnimation)
                addAnimation(rotate)
            }
            fab.startAnimation(animationSet)

            // Show hint arrow and text
            binding.hintArrow.visibility = View.VISIBLE
            binding.hintText.visibility = View.VISIBLE
        } else {
            fab.clearAnimation()

            // hide hint aroow and text
            binding.hintArrow.visibility = View.INVISIBLE
            binding.hintText.visibility = View.INVISIBLE
        }
    }
}