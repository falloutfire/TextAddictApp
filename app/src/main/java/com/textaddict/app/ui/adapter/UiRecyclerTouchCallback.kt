package com.textaddict.app.ui.adapter

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.graphics.*
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class UiRecyclerTouchCallback<T : Any>(
    b: TouchCallbackBuilder<T>,
    val adapter: ArticleViewAdapter
) : ItemTouchHelper.Callback() {

    private val mClearPaint: Paint = Paint()
    private val paint = Paint()

    private var leftBackgroundColor: Int
    private var leftIcon: Bitmap
    private var rightBackgroundColor: Int
    private var rightIcon: Bitmap
    private var onSwipeTouchListener: OnSwipeTouchListener
    private var view: View
    private var iconSize: Float
    private var isMarginAppbar: Boolean = false
    private var rightTextSnackBar: String = ""
    private var leftTextSnackBar: String = ""

    private val COLLAPSE_INTERPOLATOR: Interpolator = AccelerateInterpolator(1f)
    private val COLLAPSE_ANIM_DURATION = 250L

    private var circlePaintLeft: Paint? = Paint(Paint.ANTI_ALIAS_FLAG)
    private var circlePaintRight: Paint? = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        this.mClearPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        this.onSwipeTouchListener = b.onSwipeTouchListener!!

        this.leftBackgroundColor = b.leftBackgroundColor
        this.leftIcon = b.leftIcon!!
        this.rightBackgroundColor = b.rightBackgroundColor
        this.rightIcon = b.rightIcon!!
        this.view = b.view!!
        this.iconSize = b.iconSize
        this.isMarginAppbar = b.isMarginAppbar

        this.rightTextSnackBar = b.rightTextSnackBar
        this.leftTextSnackBar = b.leftTextSnackBar

        circlePaintLeft?.apply {
            color = leftBackgroundColor
        }

        circlePaintRight?.apply {
            color = rightBackgroundColor
        }
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (viewHolder is ArticleViewAdapter.ViewHolderArchive) return makeMovementFlags(0, 0)
        return makeMovementFlags(0, ItemTouchHelper.LEFT /*or ItemTouchHelper.RIGHT*/)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        var snackBar: Snackbar? = null
        val set = AnimatorSet()
        val unSet = AnimatorSet()

        val animHeight = LayoutParamHeightAnimator.collapse(viewHolder.itemView)
        val animBack = LayoutParamHeightAnimator.uncollapse(viewHolder.itemView)

        animHeight.setDuration(COLLAPSE_ANIM_DURATION).interpolator = COLLAPSE_INTERPOLATOR
        animBack.setDuration(COLLAPSE_ANIM_DURATION).interpolator = COLLAPSE_INTERPOLATOR

        set.play(animHeight)

        if (direction == ItemTouchHelper.LEFT) {
            snackBar = createSnackBar(leftTextSnackBar)
        }

        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                snackBar!!.show()
                adapter.removeItem(viewHolder.adapterPosition)
                onSwipeTouchListener.onSwipeLeft(viewHolder)
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        })

        set.start()

        snackBar!!.setAction("UNDO") {
            val back = (viewHolder as ArticleViewAdapter.ViewHolderArticle).mBackground
            back.setBackgroundColor(Color.TRANSPARENT)
            snackBar.dismiss()
            unSet.play(animBack)
            unSet.start()
            onSwipeTouchListener.onSwipeUndo(viewHolder)
            adapter.restoreItem()
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val CIRCLE_ACCELERATION = 2F
        val threshold = 0.3f

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val itemHeight = itemView.height.toFloat()
            val itemWidth = itemView.width.toFloat()

            val isCancelled = dX == 0f && !isCurrentlyActive

            if (isCancelled) {
                clearCanvas(
                    c,
                    itemView.right + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                return
            }

            if (itemHeight > 0) {

                val circleRadius =
                    (Math.abs(dX / itemView.width) - threshold) * itemView.width * CIRCLE_ACCELERATION

                val left = itemView.left.toFloat()
                val top = itemView.top.toFloat()
                val right = itemView.right.toFloat()
                val bottom = itemView.bottom.toFloat()

                val centerY = (top + bottom) / 2

                val margin = itemWidth * 0.025f

                if (dX < 0) {
                    paint.color = Color.parseColor("#FF333C46")
                    val background = RectF(right + dX - 25, top, right, bottom)
                    c.drawRect(background, paint)

                    c.clipRect(background)
                    val iconRight = right - margin

                    if (circleRadius > 0f) {
                        val cy = centerY
                        val cx = iconRight - (iconSize / 2)

                        c.drawCircle(cx, cy, circleRadius, circlePaintRight!!)
                    }

                    val iconRect = RectF(
                        iconRight - iconSize,
                        centerY - iconSize / 2,
                        iconRight,
                        centerY + iconSize / 2
                    )
                    c.drawBitmap(rightIcon, null, iconRect, paint)
                } else {
                    paint.color = Color.parseColor("#FF333C46")
                    val background = RectF(left, top, dX + 25, bottom)
                    c.drawRect(background, paint)

                    c.clipRect(background)
                    val iconLeft = left + margin

                    if (circleRadius > 0f) {
                        val cy = centerY
                        val cx = iconLeft + (iconSize / 2)

                        c.drawCircle(cx, cy, circleRadius, circlePaintLeft!!)
                    }

                    val iconRect = RectF(
                        iconLeft,
                        centerY - iconSize / 2,
                        iconLeft + iconSize,
                        centerY + iconSize / 2
                    )
                    c.drawBitmap(leftIcon, null, iconRect, paint)
                }
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            dX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.3f
    }

    private fun createSnackBar(text: String): Snackbar {
        val snackBar = Snackbar
            .make(
                view,
                text,
                Snackbar.LENGTH_LONG
            )

        snackBar.setActionTextColor(Color.YELLOW)

        if (isMarginAppbar) {
            val typedValue = TypedValue()
            val marginSizeAttr = intArrayOf(android.R.attr.actionBarSize)
            val indexOfAttrMarginSize = 0
            val a = obtainStyledAttributes(view.context, typedValue.data, marginSizeAttr)
            val marginSize = a.getDimensionPixelSize(indexOfAttrMarginSize, 0)
            a.recycle()

            snackBar.apply {
                view.layoutParams = (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
                    setMargins(
                        leftMargin,
                        topMargin,
                        rightMargin,
                        marginSize
                    )
                }
            }
        }
        return snackBar
    }
}

class TouchCallbackBuilder<T : Any>(val adapter: ArticleViewAdapter) {

    var onSwipeTouchListener: OnSwipeTouchListener? = null
    var view: View? = null
    var leftBackgroundColor: Int = 0
    var leftIcon: Bitmap? = null
    var rightBackgroundColor: Int = 0
    var rightIcon: Bitmap? = null
    var isMarginAppbar: Boolean = false
    var iconSize: Float = 0F
    var rightTextSnackBar: String = ""
    var leftTextSnackBar: String = ""

    fun rightTextSnackBar(rightTextSnackBar: String): TouchCallbackBuilder<T> {
        this.rightTextSnackBar = rightTextSnackBar
        return this
    }

    fun leftTextSnackBar(leftTextSnackBar: String): TouchCallbackBuilder<T> {
        this.leftTextSnackBar = leftTextSnackBar
        return this
    }

    fun leftBackgroundColor(leftBackgroundColor: Int): TouchCallbackBuilder<T> {
        this.leftBackgroundColor = leftBackgroundColor
        return this
    }

    fun leftIcon(leftIcon: Bitmap): TouchCallbackBuilder<T> {
        this.leftIcon = leftIcon
        return this
    }

    fun rightBackgroundColor(rightBackgroundColor: Int): TouchCallbackBuilder<T> {
        this.rightBackgroundColor = rightBackgroundColor
        return this
    }

    fun rightIcon(rightIcon: Bitmap): TouchCallbackBuilder<T> {
        this.rightIcon = rightIcon
        return this
    }

    fun iconSize(iconSize: Float): TouchCallbackBuilder<T> {
        this.iconSize = iconSize
        return this
    }

    fun isMarginAppbar(isMarginAppbar: Boolean): TouchCallbackBuilder<T> {
        this.isMarginAppbar = isMarginAppbar
        return this
    }

    fun onSwipeListener(onSwipeTouchListener: OnSwipeTouchListener): TouchCallbackBuilder<T> {
        this.onSwipeTouchListener = onSwipeTouchListener
        return this
    }

    fun view(view: View): TouchCallbackBuilder<T> {
        this.view = view
        return this
    }

    fun build(): UiRecyclerTouchCallback<T> {
        return UiRecyclerTouchCallback(this, adapter)
    }
}

interface OnSwipeTouchListener {

    fun onSwipeRight(vh: RecyclerView.ViewHolder)

    fun onSwipeLeft(vh: RecyclerView.ViewHolder)

    fun onSwipeUndo(vh: RecyclerView.ViewHolder)
}