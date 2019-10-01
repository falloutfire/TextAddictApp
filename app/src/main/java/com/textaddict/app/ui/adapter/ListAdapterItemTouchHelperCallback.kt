package com.textaddict.app.ui.adapter

import android.graphics.*
import android.view.MotionEvent
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.ItemTouchHelper.RIGHT
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.max
import kotlin.math.min


class ListAdapterItemTouchHelperCallback<T : Any>(
    private val adapter: ItemTouchHelperAdapter<T>,
    private val iconRight: Bitmap,
    private val iconLeft: Bitmap
) :
    ItemTouchHelper.Callback() {

    private var swipeBack = false

    private var buttonShowedState = ButtonsState.GONE

    private var currentItemViewHolder: RecyclerView.ViewHolder? = null

    private val buttonWidth = 300f

    private var buttonInstance: RectF? = null

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT or RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        if (direction == LEFT) {
            //adapter.removeItem(position)
        } else {
            //adapter.restoreItem(position)
        }
    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
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

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                var setDx = 0F
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                    setDx = max(dX, buttonWidth)
                }
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                    setDx = min(dX, -buttonWidth)
                }

                onDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )

                super.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    setDx,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            } else {
                onDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                setTouchListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
            }

            if (buttonShowedState == ButtonsState.GONE) {
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
            currentItemViewHolder = viewHolder


        }
        //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    //@SuppressLint("ClickableViewAccessibility")
    private fun setTouchListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            swipeBack =
                event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP
            if (swipeBack) {
                if (dX < -buttonWidth) {
                    buttonShowedState = ButtonsState.RIGHT_VISIBLE
                } else if (dX > buttonWidth) {
                    buttonShowedState = ButtonsState.LEFT_VISIBLE
                }
                if (buttonShowedState != ButtonsState.GONE) {
                    setTouchDownListener(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    setItemsClickable(recyclerView, false)

                }
            }
            false
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable
        }
    }

    private fun setTouchDownListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        currentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    currentlyActive
                )
            }
            false
        }
    }

    private fun setTouchUpListener(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        currentlyActive: Boolean
    ) {
        recyclerView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super@ListAdapterItemTouchHelperCallback.onChildDraw(
                    c,
                    recyclerView,
                    viewHolder,
                    0f,
                    dY,
                    actionState,
                    currentlyActive
                )
                recyclerView.setOnTouchListener { v, event -> false }
                setItemsClickable(recyclerView, true)
                swipeBack = false

                /*if (buttonsActions != null && buttonInstance != null && buttonInstance.contains(event.x, event.y)) {
                    if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                        buttonsActions.onLeftClicked(viewHolder.adapterPosition)
                    } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                        buttonsActions.onRightClicked(viewHolder.adapterPosition)
                    }
                }*/
                buttonShowedState = ButtonsState.GONE
                currentItemViewHolder = null
            }
            false
        }
    }


    fun onDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val p = Paint()
        val itemView = viewHolder.itemView
        val height = itemView.height
        val width = height / 3

        if (dX > 0) {
            p.color = Color.parseColor("#388E3C")
            val background = RectF(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            c.drawRect(background, p)

            val iconDest = RectF(
                itemView.left.toFloat() + width,
                itemView.top.toFloat() + width,
                itemView.left.toFloat() + 2 * width,
                itemView.bottom.toFloat() - width
            )
            c.drawBitmap(iconLeft, null, iconDest, p)
        } else {
            p.color = Color.parseColor("#D32F2F")
            val background = RectF(
                itemView.right.toFloat() + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            c.drawRect(background, p)
            val iconDest = RectF(
                itemView.right.toFloat() - 2 * width,
                itemView.top.toFloat() + width,
                itemView.right.toFloat() - width,
                itemView.bottom.toFloat() - width
            )
            c.drawBitmap(iconRight, null, iconDest, p)
        }
    }

    fun onDraw(c: Canvas, iconRight: Bitmap, iconLeft: Bitmap) {
        if (currentItemViewHolder != null) {
            drawButtons(c, currentItemViewHolder!!, iconRight, iconLeft)
        }
    }

    private fun drawButtons(
        c: Canvas,
        viewHolder: RecyclerView.ViewHolder,
        iconRight: Bitmap,
        iconLeft: Bitmap
    ) {
        val buttonWidthWithoutPadding = buttonWidth - 20

        val itemView = viewHolder.itemView
        val p = Paint()
        val height = itemView.height
        val width = height / 3


        //c.drawRoundRect(leftButton, corners, corners, p)
        //drawText("EDIT", c, leftButton, p)


        //c.drawRoundRect(rightButton, corners, corners, p)
        //drawText("DELETE", c, rightButton, p)

        /* val p = Paint()
         val itemView = viewHolder.itemView
         */

        buttonInstance = null
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {

            val leftButton = RectF(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            p.color = Color.parseColor("#388E3C")
            c.drawRect(leftButton, p)
            val iconDest = RectF(
                itemView.left.toFloat() + width,
                itemView.top.toFloat() + width,
                itemView.left.toFloat() + 2 * width,
                itemView.bottom.toFloat() - width
            )

            c.drawBitmap(iconLeft, null, iconDest, p)

            /*p.color = Color.parseColor("#D32F2F")
            val leftBackground = RectF(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.left.toFloat(),
                itemView.bottom.toFloat()
            )
            c.drawRect(leftBackground, p)
            val iconDest = RectF(
                itemView.right.toFloat() - 2 * width,
                itemView.top.toFloat() + width,
                itemView.right.toFloat() - width,
                itemView.bottom.toFloat() - width
            )
            c.drawBitmap(iconLeft, null, iconDest, p)*/
            buttonInstance = leftButton
        } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {

            val rightButton = RectF(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            p.color = Color.parseColor("#D32F2F")
            c.drawRect(rightButton, p)
            val iconDest = RectF(
                itemView.right.toFloat() - 2 * width,
                itemView.top.toFloat() + width,
                itemView.right.toFloat() - width,
                itemView.bottom.toFloat() - width
            )
            c.drawBitmap(iconRight, null, iconDest, p)
            /* p.color = Color.parseColor("#388E3C")
             val rightBackground = RectF(
                 itemView.right.toFloat(),
                 itemView.top.toFloat(),
                 itemView.right.toFloat(),
                 itemView.bottom.toFloat()
             )
             c.drawRect(rightBackground, p)

             val rightIconDest = RectF(
                 itemView.left.toFloat() + width,
                 itemView.top.toFloat() + width,
                 itemView.left.toFloat() + 2 * width,
                 itemView.bottom.toFloat() - width
             )
             c.drawBitmap(iconRight, null, rightIconDest, p)*/
            buttonInstance = rightButton
        }
    }
}

internal enum class ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}