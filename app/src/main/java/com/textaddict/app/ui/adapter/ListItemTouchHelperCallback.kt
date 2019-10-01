package com.textaddict.app.ui.adapter

import android.graphics.*
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class ListItemTouchHelperCallback<T : Any>(
    private val adapter: ItemTouchHelperAdapter<T>,
    private val iconRight: Bitmap,
    private val iconLeft: Bitmap, private val view: FragmentActivity
) :
    ItemTouchHelper.Callback() {

    private var buttonShowedState = ButtonsState.GONE

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
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

        if (direction == ItemTouchHelper.LEFT) {
            //adapter.removeItem(position)
            val snackbar = Snackbar.make(
                view.window.decorView.rootView,
                " removed from Recyclerview!",
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction("UNDO", View.OnClickListener {
                // undo is selected, restore the deleted item
                //adapter.restoreItem(deletedModel, deletedPosition)
            })
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
            adapter.removeItem(position)
        } else {
            val snackbar = Snackbar.make(
                view.window.decorView.rootView,
                " archieved from Recyclerview!",
                Snackbar.LENGTH_LONG
            )
            snackbar.setAction("UNDO", View.OnClickListener {
                // undo is selected, restore the deleted item
                adapter.removeItem(position)
            })
            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
            adapter.removeItem(position)
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
        if (actionState == ACTION_STATE_SWIPE) {

            val p = Paint()

            val itemView = viewHolder.itemView
            val height = itemView.bottom.toFloat() - itemView.top.toFloat()
            val width = height / 3

            if (dX > 0) {
                p.color = Color.parseColor("#388E3C")
                val background = RectF(
                    itemView.left.toFloat(),
                    itemView.top.toFloat(),
                    dX,
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, p)
                val icon_dest = RectF(
                    itemView.left.toFloat() + width,
                    itemView.top.toFloat() + width,
                    itemView.left.toFloat() + 2 * width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(iconLeft, null, icon_dest, p)
            } else {
                p.color = Color.parseColor("#D32F2F")
                val background = RectF(
                    itemView.right.toFloat() + dX,
                    itemView.top.toFloat(),
                    itemView.right.toFloat(),
                    itemView.bottom.toFloat()
                )
                c.drawRect(background, p)
                val icon_dest = RectF(
                    itemView.right.toFloat() - 2 * width,
                    itemView.top.toFloat() + width,
                    itemView.right.toFloat() - width,
                    itemView.bottom.toFloat() - width
                )
                c.drawBitmap(iconRight, null, icon_dest, p)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}