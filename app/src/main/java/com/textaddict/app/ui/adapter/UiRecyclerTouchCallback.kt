package com.textaddict.app.ui.adapter

import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.TintTypedArray.obtainStyledAttributes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class UiRecyclerTouchCallback<T : Any>(
    /*private val mContext: View,
    private val adapter: ItemTouchHelperAdapter<T>,
    private val iconLeft: Drawable,
    private val iconRight: Drawable,
    private val recyclerView: RecyclerView,
    private val isMarginAppbar: Boolean = false*/
    b: TouchCallbackBuilder<T>
) : ItemTouchHelper.Callback() {

    private val mClearPaint: Paint = Paint()
    private val paint = Paint()

    private val mBackground: ColorDrawable = ColorDrawable()
    private val backgroundColor: Int = Color.parseColor("#b80f0a")
    private val intrinsicWidth: Int = 0
    private val intrinsicHeight: Int = 0

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
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        var snackBar: Snackbar? = null

        if (direction == ItemTouchHelper.LEFT) {
            onSwipeTouchListener.onSwipeLeft(viewHolder)
            snackBar = createSnackBar(leftTextSnackBar)

        } else if (direction == ItemTouchHelper.RIGHT) {
            onSwipeTouchListener.onSwipeRight(viewHolder)
            snackBar = createSnackBar(rightTextSnackBar)
        }

        snackBar!!.setAction("UNDO") {
            onSwipeTouchListener.onSwipeUndo(viewHolder, direction)
        }

        snackBar.show()
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        viewHolder1: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            val itemHeight = itemView.height
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
                super.onChildDrawOver(
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
                val left = itemView.left.toFloat()
                val top = itemView.top.toFloat()
                val right = itemView.right.toFloat()
                val bottom = itemView.bottom.toFloat()

                val centerY = (top + bottom) / 2

                val margin = itemWidth * 0.025f

                if (dX > 0) {
                    paint.color = leftBackgroundColor
                    val background = RectF(left, top, dX, bottom)
                    c.drawRect(background, paint)

                    val iconLeft = left + margin
                    val iconRect = RectF(
                        iconLeft,
                        centerY - iconSize / 2,
                        iconLeft + iconSize,
                        centerY + iconSize / 2
                    )
                    c.drawBitmap(leftIcon, null, iconRect, paint)
                } else {
                    paint.color = rightBackgroundColor
                    val background = RectF(right + dX, top, right, bottom)
                    c.drawRect(background, paint)

                    val iconRight = right - margin
                    val iconRect = RectF(
                        iconRight - iconSize,
                        centerY - iconSize / 2,
                        iconRight,
                        centerY + iconSize / 2
                    )
                    c.drawBitmap(rightIcon, null, iconRect, paint)
                }
            }
        }

        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)


        /*val itemView = viewHolder.itemView
        val itemHeight = itemView.height

        val isCancelled = dX == 0f && !isCurrentlyActive

        if (isCancelled) {
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        mBackground.color = backgroundColor
        mBackground.setBounds(
            itemView.right + dX.toInt() + 10
            itemView.left,
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        mBackground.draw(c)

        val deleteIconTop = itemView.top + (itemHeight - intrinsicHeight) / 2
        val deleteIconMargin = (itemHeight - intrinsicHeight) / 2
        val deleteIconLeft = itemView.right - deleteIconMargin - intrinsicWidth
        val deleteIconRight = itemView.right - deleteIconMargin
        val deleteIconBottom = deleteIconTop + intrinsicHeight
        iconRight.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
        iconRight.draw(c)*/

        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(left, top, right, bottom, mClearPaint)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
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
            val textSizeAttr = intArrayOf(android.R.attr.actionBarSize)
            val indexOfAttrTextSize = 0
            val a = obtainStyledAttributes(view.context, typedValue.data, textSizeAttr)
            val textSize = a.getDimensionPixelSize(indexOfAttrTextSize, 0)
            a.recycle()

            snackBar.apply {
                view.layoutParams = (view.layoutParams as CoordinatorLayout.LayoutParams).apply {
                    setMargins(
                        leftMargin,
                        topMargin,
                        rightMargin,
                        textSize
                    )
                }
            }
        }
        return snackBar
    }
}

class TouchCallbackBuilder<T : Any> {

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
        return UiRecyclerTouchCallback(this)
    }
}

interface OnSwipeTouchListener {

    fun onSwipeRight(vh: RecyclerView.ViewHolder)

    fun onSwipeLeft(vh: RecyclerView.ViewHolder)

    fun onSwipeUndo(vh: RecyclerView.ViewHolder, direction: Int)

}