package com.textaddict.app.ui.adapter

import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.selection.SelectionTracker
import com.textaddict.app.R

class ActionModeController(private val tracker: SelectionTracker<*>) : ActionMode.Callback {

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.action_cancel -> {
                mode.finish()
                true
            }
            R.id.action_delete -> {
                mode.finish()
                true
            }
            else -> false
        }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        tracker.clearSelection()
    }
}