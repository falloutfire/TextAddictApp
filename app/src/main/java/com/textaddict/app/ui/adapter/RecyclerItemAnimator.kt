package com.textaddict.app.ui.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView


class RecyclerItemAnimator(val animationEndListener: OnAnimationEndListener) :
    DefaultItemAnimator() {

    val COLLAPSE_INTERPOLATOR: Interpolator = AccelerateInterpolator(3f)
    val COLLAPSE_ANIM_DURATION = 300L

    override fun canReuseUpdatedViewHolder(viewHolder: RecyclerView.ViewHolder): Boolean {
        return true
    }

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder,
        newHolder: RecyclerView.ViewHolder,
        preInfo: ItemHolderInfo,
        postInfo: ItemHolderInfo
    ): Boolean {

        val itemView = newHolder.itemView
        val set = AnimatorSet()

        val animHeight = LayoutParamHeightAnimator.collapse(itemView)
        animHeight.setDuration(COLLAPSE_ANIM_DURATION).interpolator = COLLAPSE_INTERPOLATOR

        set.play(animHeight)

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                dispatchChangeFinished(newHolder, false)
                animationEndListener.onChangeEnd(newHolder)
            }

            override fun onAnimationStart(animation: Animator?) {
                dispatchChangeStarting(newHolder, false)
            }
        })

        set.start()

        return false
    }

    interface OnAnimationEndListener {
        fun onChangeEnd(newHolder: RecyclerView.ViewHolder)
    }

}