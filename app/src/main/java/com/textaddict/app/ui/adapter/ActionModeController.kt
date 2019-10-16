package com.textaddict.app.ui.adapter

import android.view.Menu
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker

class ActionModeController(
    private val tracker: SelectionTracker<*>,
    private val itemHelper: UiRecyclerTouchCallback<*>
) : ActionMode.Callback {

    var onActionItemClickListener: OnActionItemClickListener? = null
    @MenuRes
    private var menuResId: Int = 0
    private var mode: ActionMode? = null

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        onActionItemClickListener?.onActionItemClick(item)
        mode.finish()
        return true
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        this.mode = mode
        mode.menuInflater.inflate(menuResId, menu)
        itemHelper.isChange = true
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        tracker.clearSelection()
        this.mode = null
        itemHelper.isChange = false
    }

    fun createActionMode(@MenuRes menuResId: Int) {
        this.menuResId = menuResId
    }

    fun closeActionMode() {
        onDestroyActionMode(this.mode)
        itemHelper.isChange = false
    }
}

interface OnActionItemClickListener {
    fun onActionItemClick(item: MenuItem)
}