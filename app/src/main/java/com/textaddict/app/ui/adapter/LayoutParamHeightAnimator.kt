package com.textaddict.app.ui.adapter

import android.animation.ValueAnimator
import android.view.View


class LayoutParamHeightAnimator(target: View, vararg values: Int) : ValueAnimator() {

    init {
        setIntValues(*values)

        addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Int
            target.layoutParams.height = value
            target.requestLayout()
        }
    }

    companion object {

        fun collapse(target: View): LayoutParamHeightAnimator {
            return LayoutParamHeightAnimator(target, target.height, 0)
        }

        fun uncollapse(target: View): LayoutParamHeightAnimator {
            return LayoutParamHeightAnimator(target, 0, target.height)
        }
    }

}
